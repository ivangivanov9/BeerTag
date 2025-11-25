package com.company.web.springdemo.controllers.mvc;

import com.company.web.springdemo.exceptions.AuthorizationException;
import com.company.web.springdemo.exceptions.EntityDuplicateException;
import com.company.web.springdemo.exceptions.EntityNotFoundException;
import com.company.web.springdemo.helpers.AuthenticationHelper;
import com.company.web.springdemo.helpers.BeerMapper;
import com.company.web.springdemo.models.*;
import com.company.web.springdemo.services.BeerService;
import com.company.web.springdemo.services.StyleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/beers")
public class BeerMvcController {

    private final BeerService beerService;
    private final StyleService styleService;
    private final BeerMapper beerMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public BeerMvcController(BeerService beerService,
                             StyleService styleService,
                             BeerMapper beerMapper,
                             AuthenticationHelper authenticationHelper) {
        this.beerService = beerService;
        this.styleService = styleService;
        this.beerMapper = beerMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("isLoggedIn")
    public boolean populateIsLoggedIn(HttpSession session) {
        return authenticationHelper.isLoggedIn(session);
    }

    @ModelAttribute("styles")
    public List<Style> populateStyles() {
        return styleService.get();
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping
    public String showAllBeers(@ModelAttribute("filterOptions") FilterOptions filterOptions,
                               Model model,
                               HttpSession session) {
        boolean isLoggedIn = authenticationHelper.isLoggedIn(session);
        model.addAttribute("isLoggedIn", isLoggedIn);

        List<Beer> beers = beerService.get(filterOptions);
        model.addAttribute("beers", beers);
        return "BeersView";
    }

    @GetMapping("/{id}")
    public String showSingleBeer(@PathVariable int id, Model model, HttpSession session) {
        try {
            Beer beer = beerService.get(id);
            model.addAttribute("beer", beer);

            boolean isLoggedIn = authenticationHelper.isLoggedIn(session);
            model.addAttribute("isLoggedIn", isLoggedIn);

            return "BeerView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/new")
    public String showNewBeerPage(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
            model.addAttribute("beer", new BeerDto());
            return "BeerCreateView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/new")
    public String createBeer(@Valid @ModelAttribute("beer") BeerDto beerDto,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "BeerCreateView";
        }

        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            Beer beer = beerMapper.fromDto(beerDto);
            beerService.create(beer, user);
            return "redirect:/beers";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("name", "duplicate_beer", e.getMessage());
            return "BeerCreateView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/{id}/update")
    public String showEditBeerPage(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            Beer beer = beerService.get(id);

            BeerDto beerDto = beerMapper.toDto(beer);
            model.addAttribute("beerId", id);
            model.addAttribute("beer", beerDto);
            return "BeerUpdateView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/{id}/update")
    public String updateBeer(@PathVariable int id,
                             @Valid @ModelAttribute("beer") BeerDto dto,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("beerId", id);
            return "BeerUpdateView";
        }

        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            Beer beer = beerMapper.fromDto(id, dto);
            beerService.update(beer, user);
            return "redirect:/beers/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("name", "duplicate_beer", e.getMessage());
            model.addAttribute("beerId", id);
            return "BeerUpdateView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteBeer(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            beerService.delete(id, user);
            return "redirect:/beers";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
    }

    @ExceptionHandler(AuthorizationException.class)
    public String handleAuthorizationException(AuthorizationException e,
                                               HttpSession session) {
        if (!authenticationHelper.isLoggedIn(session)) {
            return "redirect:/auth/login";
        } else {
            return "AccessDeniedView";
        }
    }
}