package uz.husan.rentcar24.controller;
import lombok.*;
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

import java.util.List;
import java.util.Optional;
@Component
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CarDAO carDAO;
    private final UserDAO userDAO;
    private final BookingDAO bookingDAO;

    @GetMapping
    public String adminHome(Model model) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser == null) {
            model.addAttribute("message", "You are not logged in");
            return "autherror";
        }
        model.addAttribute("currentUser", currentUser);
        return "admin/admin";
    }

    @PostMapping("/save")
    public String saveCar(@ModelAttribute Car car) {
        carDAO.updateCar(car);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editCarForm(@PathVariable("id") int carId, Model model) {
        Optional<Car> car = carDAO.getCarById(carId);
        if (!car.isPresent()) {
            model.addAttribute("message", "Mashina topilmadi.");
            return "error/adminerror";
        }
        model.addAttribute("car", car.get());
        return "admin/edit-form";
    }

    @GetMapping("/edited")
    public String showAllCarsForEdit(Model model) {
        List<Car> carList = carDAO.getAllCars();  // barcha mashinalarni olish
        model.addAttribute("carList", carList);
        return "admin/edit";  // HTML fayl nomi
    }
    // Bitta userning buyurtmalari (PathVariable bilan)
    @GetMapping("/buyurtmalar/{id}")
    public String userOrders(@PathVariable("id") int userId, Model model) {
        // Foydalanuvchini olish
        User user = userDAO.getUserById(userId);

        if (user == null) {
            model.addAttribute("message", "Foydalanuvchi topilmadi.");
            return "error/adminerror";
        }

        // Foydalanuvchiga tegishli buyurtmalarni olish
        List<Booking> bookings = bookingDAO.getAllBookingsByUser(user);

        // DTO'ga o'tkazish (buildersiz)
        List<BookingDTO> dtos = bookings.stream().map(b -> {
            BookingDTO dto = new BookingDTO();
            dto.setId(b.getId());
            dto.setImage(b.getCar().getImageUrl());
            dto.setTitle(b.getCar().getModel());
            dto.setStartDate(b.getStartDate());
            dto.setEndDate(b.getEndDate());
            dto.setPrice(String.format("%,.0f so'm", b.getPrice()));
            return dto;
        }).toList();

        model.addAttribute("bookingList", dtos);
        return "admin/buyurtmalar";
    }




    // Barcha buyurtmalar tarixi
    @GetMapping("/orders")
    public String allOrders(Model model) {
        List<Booking> bookings = bookingDAO.getAllBooking();

        List<BookingDTO> dtos = bookings.stream().map(b -> {
            BookingDTO dto = new BookingDTO();
            dto.setId(b.getId());
            dto.setImage(b.getCar().getImageUrl());
            dto.setTitle(b.getCar().getModel());
            dto.setStartDate(b.getStartDate());
            dto.setEndDate(b.getEndDate());
            dto.setPrice(String.format("%,.0f so'm", b.getPrice()));
            return dto;
        }).toList();

        model.addAttribute("bookingList", dtos);
        return "admin/orders";
    }


    @GetMapping("/addCar")
    public String addCarForm(Model model) {
        model.addAttribute("car", new Car());
        return "admin/addcar";
    }

    @PostMapping("/addCar")
    public String addCarSubmit(Car car, Model model) {
        Optional<Car> exists = carDAO.getCarByNumber(car.getNumber());
        if (exists.isPresent()) {
            model.addAttribute("message", "This car already exists");
            return "error/adminerror";
        }
        carDAO.addCar(car);
        return "redirect:/admin";
    }

    @GetMapping("/viewCars")
    public String viewCars(Model model) {
        model.addAttribute("cars", carDAO.getAllCars());
        return "admin/viewcars";
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        var usersOpt = userDAO.getAllUsersByUserRole();
        if (usersOpt.isEmpty()) {
            model.addAttribute("message", "No users found");
            return "error/adminerror";
        }
        model.addAttribute("users", usersOpt.get());
        return "admin/users";
    }
}
