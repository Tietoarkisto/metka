package fi.uta.fsd.metka.mvc.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fi.uta.fsd.metka.data.entity.MaterialEntity;
import fi.uta.fsd.metka.mvc.domain.DomainFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller("materialController")
public class MaterialController {
	
	@Autowired
	private DomainFacade domain;
	
	private static final Logger logger = LoggerFactory.getLogger(MaterialController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		//MaterialEntity material = materialService.getMaterialService(1l);

        List<MaterialEntity> materials = domain.findAllMaterials();
		String formattedDate = dateFormat.format(date);
		
//		model.addAttribute("materialName", material.getName());
		model.addAttribute("serverTime", formattedDate );
        model.addAttribute("materials", materials);
		
		return "home";
	}
	
}
