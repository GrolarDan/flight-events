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

import cz.masci.flightevents.model.dto.EventDTO;
import cz.masci.flightevents.model.events.BaseEvent;
import cz.masci.flightevents.model.events.MotionEvent;
import cz.masci.flightevents.services.EventMapper;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 *
 * @author Daniel Masek
 */
@Service
public class EventMapperImpl implements EventMapper {

    @Override
    public <T extends BaseEvent> EventDTO map(T event) {
        var result = new EventDTO();
        result.setType(event.getEventType());
        result.setStartTime(event.getStartTime());
        result.setDuration(Optional.ofNullable(event.getDuration()).orElse(0.0));
        result.setMessage(getMessage(event));

        return result;
    }

    private <T extends BaseEvent> String getMessage(T event) {
        if (event instanceof MotionEvent motionEvent) {
            return getMessage(motionEvent);
        }

//        if (event instanceof VoiceMessageEvent voiceMessageEvent) {
//            return getMessage(voiceMessageEvent);
//        }
//
//        if (event instanceof ConditionEvent conditionEvent) {
//            return getMessage(conditionEvent);
//        }

        return "NOT IMPLEMENTED";
    }

    private String getMessage(MotionEvent event) {
        return String.format("'%s %2.2f: %2.2f'", event.getAxis(), event.getVelocity(), event.getAcceleration());
    }

//    private String getMessage(VoiceMessageEvent event) {
//        return "NOT IMPLEMENTED";
//    }
//
//    private String getMessage(ConditionEvent event) {
//        return "NOT IMPLEMENTED";
//    }

}
