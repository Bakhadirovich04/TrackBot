package uz.husan.rentcar24.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.husan.rentcar24.dao.BookingDAO;
import uz.husan.rentcar24.dao.CarDAO;
import uz.husan.rentcar24.dao.UserDAO;
import uz.husan.rentcar24.entity.Booking;
import uz.husan.rentcar24.entity.Car;
import uz.husan.rentcar24.entity.User;
import uz.husan.rentcar24.entity.enums.Status;
import uz.husan.rentcar24.entity.enums.UserRole;

import javax.management.relation.Role;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {
    private final BookingDAO bookingDAO;
    private final CarDAO carDAO;
    private final UserDAO userDAO;
    @PostMapping("/takecar")
    public String takeCar(@RequestParam("car_id") int carId,
                          @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
                          @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
                          Principal principal, Model model) {
        Optional<Car> carById = carDAO.getCarById(carId);
        if (!carById.isPresent()) {
            model.addAttribute("message", "Car not found");
            return "error/usererror";

        }
        Car car = carById.get();
        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int i =endDate.getDayOfMonth() - startDate.getDayOfMonth();
        if(i<0)i*=(-1);
        double pr = car.getPrice();
        Double price=i*pr;
        User user1 =userDAO.getUserById(user.getId());
        if(user1.getBalance()<price){
            model.addAttribute("message", "You are not enough money");
            return "error/usererror";
        }
        User admin =userDAO.getUserByRole("ADMIN");
        Double balance= user1.getBalance()-price;
        Double balanceAdmin= admin.getBalance()+price;
        admin.setBalance(balanceAdmin);
        userDAO.updateUser(admin);
        user1.setBalance(balance);
        userDAO.updateUser(user1);
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCar(car);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setPrice(price);
        booking.setStatus(Status.GIVEN);
        car.setStatus(Status.GIVEN);
        carDAO.updateCar(car);
        bookingDAO.addBooking(booking);

        return "redirect:/user";
    }

}
