package uz.husan.rentcar24.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.husan.rentcar24.dao.BookingDAO;
import uz.husan.rentcar24.dao.CarDAO;
import uz.husan.rentcar24.dao.UserDAO;
import uz.husan.rentcar24.dto.BookingDTO;
import uz.husan.rentcar24.entity.Booking;
import uz.husan.rentcar24.entity.Car;
import uz.husan.rentcar24.entity.User;
import uz.husan.rentcar24.entity.enums.CarRate;
import uz.husan.rentcar24.entity.enums.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserDAO userDAO;
    private final CarDAO carDAO;
    private final BookingDAO bookingDAO;

    @GetMapping
    public String userPage(Model model) {
        // Hozirgi logindan olingan user
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Optional<User> user = userDAO.getUserByEmail(currentUser.getEmail());
        if (user.isEmpty()) {
            model.addAttribute("message", "You are not logged in");
            return "autherror";
        }
        model.addAttribute("currentUser", user.get());
        return "user/user";
    }
    @GetMapping("/ijaralar")
    public String ijaralar(Model model) {
        // Hozirgi userni olish
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Foydalanuvchiga tegishli ijaralarni olish
        List<Booking> bookings = bookingDAO.getAllBookingsByUser(currentUser);

        // Tugagan ijaralardagi mashinalarni yangilash
        bookings.stream()
                .filter(b -> b.getEndDate().isBefore(LocalDateTime.now()))
                .forEach(b -> {
                    Car car = b.getCar();
                    if (car != null) {
                        car.setStatus(Status.NOTGIVEN);
                        carDAO.updateCar(car);
                    }
                });

        // Faqat hali tugamagan va car statusi GIVEN bo'lganlar uchun
        List<BookingDTO> dtos = bookings.stream()
                .filter(b -> b.getEndDate().isAfter(LocalDateTime.now()))
                .map(b -> {
                    BookingDTO dto = new BookingDTO();
                    dto.setId(b.getId());
                    dto.setImage(b.getCar().getImageUrl());
                    dto.setTitle(b.getCar().getModel());
                    dto.setStartDate(b.getStartDate());
                    dto.setEndDate(b.getEndDate());
                    dto.setPrice(String.format("%,.0f so'm", b.getPrice()));
                    dto.setCarStatus(b.getCar().getStatus()); // ENUM bo‘lganligi uchun .name() yo‘q
                    return dto;
                })
                .collect(Collectors.toList());

        model.addAttribute("bookingList", dtos);
        return "user/ijaralar";
    }



    @GetMapping("/bringCarByRate")
    public String bringCarByRate(@RequestParam("rate") CarRate rate, Model model, HttpServletRequest req, HttpServletResponse resp) {
        List<Car> cars =carDAO.getCarsByRate(String.valueOf(rate));
        model.addAttribute("cars", cars);
        return "user/bringCarByRate";
    }
    @GetMapping("/selectDate")
    public String selectDatePage(@RequestParam("carId") int carId, Model model) {
        model.addAttribute("carId", carId);
        return "user/selectdate";
    }
    @GetMapping("/bringcar")
    public String bringcar(Model model, HttpServletRequest req, HttpServletResponse resp) {
        return "user/bringcar";
    }


}
