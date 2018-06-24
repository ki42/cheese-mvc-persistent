package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {
    @Autowired
    MenuDao menuDao;

    @Autowired
    CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {
        Iterable menuDaoAll = menuDao.findAll();
        model.addAttribute("menus", menuDaoAll);
        model.addAttribute("title", "Menus");
        return "menu/index";
    }

    @RequestMapping(value = "add", method= RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute("menu", new Menu());
        model.addAttribute("title", "New Menu");
        return "menu/add";
    }

    @RequestMapping(value = "add", method= RequestMethod.POST)
    public String add(@ModelAttribute @Valid Menu amenu,
                               Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "New Menu");
            model.addAttribute("menu", amenu);
            return "menu/add";
        }
        menuDao.save(amenu);
        //fairly sure I need to pass things into the new view:
        model.addAttribute("menu", amenu);
        model.addAttribute("title", "New Menu");
        return "redirect:view/" + amenu.getId();
    }

    //TODO check the syntax for value - make sure it works
    @RequestMapping(value = "view/{id}", method= RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id) {
        Menu aMenu = menuDao.findOne(id);
        model.addAttribute("menuId", aMenu.getId());
        model.addAttribute("title", aMenu.getName());
        model.addAttribute("cheeses", aMenu.getCheeses());
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method= RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id) {
        Menu aMenu = menuDao.findOne(id);
        Iterable<Cheese> all = cheeseDao.findAll();
        AddMenuItemForm form = new AddMenuItemForm(aMenu, all); // TODO list of all Cheese items in the database, make sure this works
        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu: " + aMenu.getName());
        model.addAttribute("menu", aMenu);
        return "menu/add-item";
    }


    @RequestMapping(value = "add-item", method= RequestMethod.POST)
    public String addItem(@ModelAttribute @Valid AddMenuItemForm form,
                          Errors errors, Model model ) {
        Menu aMenu = menuDao.findOne(form.getMenuId());
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Item");
            model.addAttribute("form", form);
            model.addAttribute("menu", aMenu);
           return "menu/add-item";
        }
        Cheese aCheese = cheeseDao.findOne(form.getCheeseId());
        aMenu.addItem(aCheese);
        menuDao.save(aMenu);
        model.addAttribute("menu", aMenu);
        return "redirect:/menu/view/" + aMenu.getId();  //TODO: make sure this works
    }

}
