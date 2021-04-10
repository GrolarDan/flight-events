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
package cz.masci.flightevents.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Daniel Masek
 */
@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class VoiceMessageEvent {
    @XmlAttribute(name = "selfID")
    private String selfId;
    @XmlAttribute(name = "startTime")
    private Double startTime;
    @XmlAttribute(name = "messageFilename")
    private String messageFilename;
    @XmlAttribute(name = "duration")
    private Double duration;
    @XmlAttribute(name = "volume")
    private Integer volume;
}
