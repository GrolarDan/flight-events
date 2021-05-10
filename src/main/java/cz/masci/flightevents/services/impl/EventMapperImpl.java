/*
 * Copyright (C) 2021 Daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.masci.flightevents.services.impl;

import cz.masci.flightevents.mapper.MappingProperties;
import cz.masci.flightevents.model.dto.ComparatorDTO;
import cz.masci.flightevents.model.dto.ConditionDTO;
import cz.masci.flightevents.model.dto.EventDTO;
import cz.masci.flightevents.model.events.BaseEvent;
import cz.masci.flightevents.model.events.ConditionEvent;
import cz.masci.flightevents.model.events.MotionEvent;
import cz.masci.flightevents.model.events.VoiceMessageEvent;
import cz.masci.flightevents.services.EventMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author Daniel Masek
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventMapperImpl implements EventMapper {

    private final MappingProperties mappingProperties;
    
    @Override
    public <T extends BaseEvent> EventDTO map(T event) {
        var result = new EventDTO();
        result.setType(event.getEventType());
        result.setStartTime(event.getStartTime());
        result.setDuration(Optional.ofNullable(event.getDuration()).orElse(1.0));
        result.setMessage(getMessage(event));

        return result;
    }

    private <T extends BaseEvent> String getMessage(T event) {
        if (event instanceof MotionEvent motionEvent) {
            return getMotionMessage(motionEvent);
        }

        if (event instanceof VoiceMessageEvent voiceMessageEvent) {
            return getVoiceMessage(voiceMessageEvent);
        }

        if (event instanceof ConditionEvent conditionEvent) {
            return getConditionMessage(conditionEvent);
        }

        return "NOT IMPLEMENTED";
    }

    private String getMotionMessage(MotionEvent event) {
        var result = new StringBuilder();
        result.append(event.getAxis()).append(" ");
        if (event.getPosition() != null) {
            result.append(event.getPosition()).append(" ");
        }
        result.append(formatDouble(event.getVelocity(), 2)).append(" ");
        result.append(formatDouble(event.getAcceleration(), 2));
        
        return result.toString();
    }

    private String getVoiceMessage(VoiceMessageEvent event) {
        var result = new StringBuilder();
        result.append(event.getMessageFilename());
        
        return result.toString();
    }

    private String getConditionMessage(ConditionEvent event) {
        var comparator = mapComparator(event.getComparator());
        var condition = mapCondition(event.getConditionId());

        var result = new StringBuilder();
        result.append(condition.getName()).append(" ");
        result.append(comparator.getSign()).append(" ");
        result.append(formatDouble(event.getConditionValue(), 2));
        
        if (condition.isPosition()) {
            result.append(" from ").append(mapPosition(event.getConditionValue2(), event.getConditionValue3()));
        }
        
        if (comparator.isBipolar()) {
            result.append(" ").append(formatDouble(event.getConditionValue2(), 2));
        }
        
        return result.toString();
    }

    private ConditionDTO mapCondition(Integer conditionId) {
        log.trace("Mapping condition: {}", conditionId);
        return Optional.ofNullable(mappingProperties.getCondition().get(conditionId))
                .orElseGet(ConditionDTO.getNotDefined());
    }

    private ComparatorDTO mapComparator(Integer comparator) {
        log.trace("Mapping comparator: {}", comparator);
        return Optional.ofNullable(mappingProperties.getComparator().get(comparator))
                .orElseGet(ComparatorDTO.getNotDefined());
    }

    private String mapPosition(Double value1, Double value2) {
        double epsilon = 0.001d;

        log.trace("Mapping position: {}:{}", value1, value2);
        
        if ((Math.abs(value1 - 21.30830833) < epsilon)
                && (Math.abs(value2 + 157.93) < epsilon)) {
            return "VOR";
        }

        if ((Math.abs(value1 - 21.32473333) < epsilon)
                && (Math.abs(value2 + 158.049) < epsilon)) {
            return "NDB";
        }
        
        return "NOT DEFINED POSITION";
    }
    
    private String formatDouble(Double value, int precision) {
        return String.format("%." + precision + "f", value);
    }
}
