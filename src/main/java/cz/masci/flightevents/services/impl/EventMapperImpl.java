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
import cz.masci.flightevents.model.dto.PositionDTO;
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
            var position = mapPosition(event.getConditionValue2(), event.getConditionValue3());
            result.append(" from ").append(position.getName());
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

    private PositionDTO mapPosition(double value1, double value2) {
        log.trace("Mapping position: {} : {}", value1, value2);

        for (PositionDTO position : mappingProperties.getPosition()) {
            if ((Math.abs(value1 - position.getValue1()) < position.getEpsilon())
                    && (Math.abs(value2 - position.getValue2()) < position.getEpsilon())) {
                return position;
            }
            
        }
        
        return PositionDTO.getNotDefined();
    }
    
    private String formatDouble(Double value, int precision) {
        return String.format("%." + precision + "f", value);
    }
}
