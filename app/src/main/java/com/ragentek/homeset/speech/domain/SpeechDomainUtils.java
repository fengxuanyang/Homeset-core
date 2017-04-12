package com.ragentek.homeset.speech.domain;

import com.alibaba.fastjson.JSON;
import static com.ragentek.homeset.speech.domain.SpeechDomainType.*;

public class SpeechDomainUtils {

    public static SpeechBaseDomain parseResult(String result) {
        SpeechBaseDomain baseDomain = JSON.parseObject(result, SpeechBaseDomain.class);

        switch (getDomainType(baseDomain)) {
            case MUSIC:
                return JSON.parseObject(result, SpeechMusicDomain.class);
            case MUSIC_PLAYER:
                return JSON.parseObject(result, SpeechMusicPlayerDomain.class);
            case TELEPHONE:
                return JSON.parseObject(result, SpeechTelephoneDomain.class);
            case WEATHER:
                return JSON.parseObject(result, SpeechWeatherDomain.class);
            case OPENQA:
                return JSON.parseObject(result, SpeechHomesetDomain.class);
        }

        return baseDomain;
    }

    public static SpeechDomainType getDomainType(SpeechBaseDomain speechDomain) {
        String service = speechDomain.service;

        if (service.equals(MUSIC.getType())) {
            return MUSIC;
        } else if (service.equals(MUSIC_PLAYER.getType())) {
            return MUSIC_PLAYER;
        } else if (service.equals(TELEPHONE.getType())) {
            return TELEPHONE;
        } else if (service.equals(WEATHER.getType())) {
            return WEATHER;
        } else if (service.equals(OPENQA.getType())) {
            if (speechDomain instanceof SpeechHomesetDomain) {
                SpeechHomesetDomain homesetDomain = (SpeechHomesetDomain) speechDomain;
                String subType = homesetDomain.answer.text;

                if (subType.equals(SpeechDomainType.HOMESET_FAVORITE.getSubType())) {
                    return HOMESET_FAVORITE;
                } else if (subType.equals(HOMESET_CROSSTALK.getSubType())) {
                    return HOMESET_CROSSTALK;
                } else if (subType.equals(HOMESET_OPERA.getSubType())) {
                    return HOMESET_OPERA;
                }else if (subType.equals(HOMESET_STROY.getSubType())) {
                    return HOMESET_STROY;
                } else if (subType.equals(HOMESET_HEALTH.getSubType())) {
                    return HOMESET_HEALTH;
                } else if (subType.equals(HOMESET_FINACE.getSubType())) {
                    return HOMESET_FINACE;
                } else if (subType.equals(HOMESET_HISTORY.getSubType())) {
                    return HOMESET_HISTORY;
                } else if (subType.equals(HOMESET_RADIO.getSubType())) {
                    return HOMESET_RADIO;
                }
            }

            return OPENQA;
        }

        return NULL;
    }
}
