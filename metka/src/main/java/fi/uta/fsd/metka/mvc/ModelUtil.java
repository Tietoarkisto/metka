package fi.uta.fsd.metka.mvc;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import fi.uta.fsd.metkaAuthentication.MetkaAuthenticationDetails;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.ui.Model;

import java.util.List;

/**
 * Provides methods for handling mvc model and groups different functionality together
 */
public final class ModelUtil {
    private ModelUtil() {}

    private static void addUserInfo(Model model) {
        MetkaAuthenticationDetails details = AuthenticationUtil.getAuthenticationDetails();
        model.asMap().put("uDisplayName", details.getDisplayName());
        model.asMap().put("uUserName", details.getUserName());
        model.asMap().put("uRole", details.getRole().toJsonString());
        model.asMap().put("uDefLang", details.getRole().getDefaultLanguage().toValue());
    }

    public static void initRevisionModel(Model model, ConfigurationType type) {
        initRevisionModel(model, type, null);
    }

    public static void initRevisionModel(Model model, ConfigurationType type, Long id) {
        initRevisionModel(model, type, id, null);
    }

    public static void initRevisionModel(Model model, ConfigurationType type, Long id, Integer no) {
        revisionModel(model, type, id, no);
    }

    private static void revisionModel(Model model, ConfigurationType type, Long id, Integer no) {
        addUserInfo(model);
        model.asMap().put("configurationType", type);
        if(id != null) model.asMap().put("revisionId", id);
        if(no != null) model.asMap().put("revisionNo", no);
    }

    public static void initExpertSearch(Model model) {
        addUserInfo(model);
        model.asMap().put("configurationType", "EXPERT");
    }

    public static void initSettings(Model model, List<Pair<String, Boolean>> pairs) {
        addUserInfo(model);
        model.asMap().put("configurationType", "SETTINGS");
        model.asMap().put("indexers", pairs);
    }

    public static void initBinder(Model model) {
        addUserInfo(model);
        model.asMap().put("configurationType", "BINDER");
    }
}
