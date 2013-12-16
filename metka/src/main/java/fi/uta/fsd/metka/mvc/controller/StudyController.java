package fi.uta.fsd.metka.mvc.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fi.uta.fsd.metka.mvc.domain.DomainFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller("studyController")
public class StudyController {

    /*@ModelAttribute("Series")
    public SeriesEntity getSeriesEntity() {
        return new SeriesEntity();
    }

	@Autowired
	private DomainFacade domain;
	
	private static final Logger logger = LoggerFactory.getLogger(StudyController.class);*/
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	/*@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model, @ModelAttribute("Series")SeriesEntity series) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		List<StudyEntity> studies = domain.listAllStudies();
        List<SeriesEntity> seriesList = domain.listAllSeries();
		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate );
        model.addAttribute("studies", studies);
		model.addAttribute("seriesList", seriesList);
        model.addAttribute("page", "series");

		return "home";
	}*/
}
