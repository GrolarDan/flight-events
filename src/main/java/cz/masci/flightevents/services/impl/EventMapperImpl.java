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
import cz.masci.flightevents.model.dto.EventType;
import cz.masci.flightevents.model.events.AbstractEvent;
import cz.masci.flightevents.model.events.ConditionEvent;
import cz.masci.flightevents.model.events.MotionEvent;
import cz.masci.flightevents.model.events.VoiceMessageEvent;
import cz.masci.flightevents.services.EventMapper;
import org.springframework.stereotype.Service;

/**
 *
 * @author Daniel Masek
 */
@Service
public class EventMapperImpl implements EventMapper {

    @Override
    public EventDTO map(AbstractEvent event) {
        if (event instanceof MotionEvent motionEvent) {
            return map(motionEvent);
        }
        
        if (event instanceof VoiceMessageEvent voiceMessageEvent) {
            return map(voiceMessageEvent);
        }
        
        if (event instanceof ConditionEvent conditionEvent) {
            return map(conditionEvent);
        }
        
        throw new UnsupportedOperationException("Unsupported event type");
    }

    private EventDTO map(MotionEvent event) {
        var result = new EventDTO();
        result.setType(EventType.MOTION);
        result.setStartTime(event.getStartTime());
        result.setDuration(event.getDuration());
        result.setMessage(String.format("'%s %2.2f: %2.2f'", event.getAxis(), event.getVelocity(), event.getAcceleration()));

        return result;
    }

    private EventDTO map(VoiceMessageEvent event) {
        var result = new EventDTO();
        result.setType(EventType.VOICE_MESSAGE);
        result.setStartTime(event.getStartTime());
        result.setDuration(event.getDuration());

        return result;
    }

    private EventDTO map(ConditionEvent event) {
        var result = new EventDTO();
        result.setType(EventType.CONDITION);
        result.setStartTime(event.getStartTime());
        result.setDuration(1.0);

        return result;
    }

}
