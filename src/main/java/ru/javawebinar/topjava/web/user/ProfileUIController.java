package ru.javawebinar.topjava.web.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileUIController extends AbstractUserController {

    private static final String EMAIL_DUPLICATE_CONSTRAINT = "users_unique_email_idx";

    @GetMapping
    public String profile() {
        return "profile";
    }

    @PostMapping
    public String updateProfile(@Valid UserTo userTo, BindingResult result, SessionStatus status) {
        if (result.hasErrors()) {
            return "profile";
        } else {
            try {
                super.update(userTo, SecurityUtil.authUserId());
                SecurityUtil.get().setTo(userTo);
                status.setComplete();
                return "redirect:/meals";
            } catch (DataIntegrityViolationException e) {
                return processDuplicateEmail(e, result);
            }
        }
    }

    @GetMapping("/register")
    public String register(ModelMap model) {
        model.addAttribute("userTo", new UserTo());
        model.addAttribute("register", true);
        return "profile";
    }

    @PostMapping("/register")
    public String saveRegister(@Valid UserTo userTo, BindingResult result, SessionStatus status, ModelMap model) {
        if (result.hasErrors()) {
            model.addAttribute("register", true);
            return "profile";
        } else {
            try {
                super.create(userTo);
                status.setComplete();
                return "redirect:/login?message=app.registered&username=" + userTo.getEmail();
            } catch (DataIntegrityViolationException e) {
                model.addAttribute("register", true);
                return processDuplicateEmail(e, result);
            }
        }
    }

    private String processDuplicateEmail(DataIntegrityViolationException e, BindingResult result) {
        String rootMessage = ValidationUtil.getRootCause(e).getMessage();
        String lowerMessage = rootMessage == null ? "" : rootMessage.toLowerCase();
        if (lowerMessage.contains(EMAIL_DUPLICATE_CONSTRAINT)) {
            result.rejectValue("email", "error.duplicateEmail");
            return "profile";
        }
        throw e;
    }
}