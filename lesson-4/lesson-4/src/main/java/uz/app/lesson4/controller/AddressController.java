package uz.app.lesson4.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.app.lesson4.dto.AddressDTO;
import uz.app.lesson4.dto.RegionDTO;
import uz.app.lesson4.entity.Address;
import uz.app.lesson4.entity.Country;
import uz.app.lesson4.entity.Region;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/address")
public class AddressController {
    private final JdbcTemplate jdbcTemplate;
    @GetMapping("/get")
    public String getRegion(Model model) {
        List<Region> regions = jdbcTemplate.query("select * from region order by id", BeanPropertyRowMapper.newInstance(Region.class));

        Map<Integer, Region> regionMap = regions.stream().collect(Collectors.toMap(c -> c.getId(), c -> c));


        List<Address> addresses = jdbcTemplate.query("select * from Address", (rs, rowNum) -> {
           Address address = new Address();
           address.setId(rs.getInt("id"));
           address.setStreet(rs.getString("street"));
           address.setHomeNumber(rs.getString("home_number"));
           address.setRegion(regionMap.get(rs.getInt("region_id")));
            return address;
        });
        model.addAttribute("regions", regions);
        model.addAttribute("addresses", addresses);
        return "address";
    }

    @PostMapping("/create")
    public String createRegion(@ModelAttribute AddressDTO addressDTO) {
        jdbcTemplate.update("insert into address(street,home_number,region_id) values(?,?,?)",addressDTO.getStreet(),addressDTO.getHomeNumber(),addressDTO.getRegionId());
        return "redirect:/address/get";
    }
}
