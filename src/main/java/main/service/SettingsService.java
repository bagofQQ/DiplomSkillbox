package main.service;

import main.api.request.SettingsRequest;
import main.api.response.SettingsResponse;
import main.model.GlobalSettings;
import main.model.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingsService {


    @Value("${blog.constants.valueYes}")
    private String VALUE_YES;
    @Value("${blog.constants.valueNo}")
    private String VALUE_NO;

    @Value("${blog.constants.multiuserMode}")
    private String MM_CODE;
    @Value("${blog.constants.postPremoderation}")
    private String PP_CODE;
    @Value("${blog.constants.statisticsIsPublic}")
    private String SIP_CODE;

    private final GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public SettingsService(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();

        Iterable<GlobalSettings> globalSettingsIterable = globalSettingsRepository.findAll();
        if (globalSettingsIterable.iterator().hasNext()) {
            for (GlobalSettings setting : globalSettingsIterable) {
                if (setting.getCode().equals(MM_CODE)) {
                    Optional<GlobalSettings> optionalGlobalSettings = globalSettingsRepository.findById(setting.getId());
                    if (optionalGlobalSettings.get().getValue().equals(VALUE_YES)) {
                        settingsResponse.setMultiuserMode(true);
                    }
                }
                if (setting.getCode().equals(PP_CODE)) {
                    Optional<GlobalSettings> optionalGlobalSettings = globalSettingsRepository.findById(setting.getId());
                    if (optionalGlobalSettings.get().getValue().equals(VALUE_YES)) {
                        settingsResponse.setPostPremoderation(true);
                    }
                }
                if (setting.getCode().equals(SIP_CODE)) {
                    Optional<GlobalSettings> optionalGlobalSettings = globalSettingsRepository.findById(setting.getId());
                    if (optionalGlobalSettings.get().getValue().equals(VALUE_YES)) {
                        settingsResponse.setStatisticsIsPublic(true);
                    }
                }
            }
            return settingsResponse;
        } else {
            saveNewOneSetting("multiuserMode", "MM");
            saveNewOneSetting("postPremoderation", "PP");
            saveNewOneSetting("statisticsIsPublic", "SIP");
            settingsResponse.setMultiuserMode(true);
            settingsResponse.setPostPremoderation(true);
            settingsResponse.setStatisticsIsPublic(true);
            return settingsResponse;
        }
    }

    public SettingsResponse putGlobalSettings(SettingsRequest settingsRequest) {

        Iterable<GlobalSettings> globalSettingsIterable = globalSettingsRepository.findAll();
        if (globalSettingsIterable.iterator().hasNext()) {
            for (GlobalSettings setting : globalSettingsIterable) {
                if (setting.getCode().equals(MM_CODE)) {
                    Optional<GlobalSettings> optionalGlobalSettings = globalSettingsRepository.findById(setting.getId());
                    if (settingsRequest.isMultiuserMode()) {
                        optionalGlobalSettings.get().setValue(VALUE_YES);
                    } else {
                        optionalGlobalSettings.get().setValue(VALUE_NO);
                    }
                    globalSettingsRepository.save(optionalGlobalSettings.get());
                }
                if (setting.getCode().equals(PP_CODE)) {
                    Optional<GlobalSettings> optionalGlobalSettings = globalSettingsRepository.findById(setting.getId());
                    if (settingsRequest.isPostPremoderation()) {
                        optionalGlobalSettings.get().setValue(VALUE_YES);
                    } else {
                        optionalGlobalSettings.get().setValue(VALUE_NO);
                    }
                    globalSettingsRepository.save(optionalGlobalSettings.get());
                }
                if (setting.getCode().equals(SIP_CODE)) {
                    Optional<GlobalSettings> optionalGlobalSettings = globalSettingsRepository.findById(setting.getId());
                    if (settingsRequest.isStatisticsIsPublic()) {
                        optionalGlobalSettings.get().setValue(VALUE_YES);
                    } else {
                        optionalGlobalSettings.get().setValue(VALUE_NO);
                    }
                    globalSettingsRepository.save(optionalGlobalSettings.get());
                }
            }
        }
        return getGlobalSettings();
    }

    public void saveNewOneSetting(String name, String code) {
        GlobalSettings globalSettings = new GlobalSettings();
        globalSettings.setName(name);
        globalSettings.setCode(code);
        globalSettings.setValue(VALUE_YES);
        globalSettingsRepository.save(globalSettings);
    }
}
