package fi.uta.fsd.metka.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Interface for variables-object and variable-object reading and modification.
 * This controller handles both the browser-requests to view the variables page
 * as well as the ajax-requests for single variable operations.
 */
@Controller
@RequestMapping("variables")
public class VariablesController {
    // TODO: If there are any actual variable or variables specific calls add them here, otherwise the general RevisionController calls should suffice
}
