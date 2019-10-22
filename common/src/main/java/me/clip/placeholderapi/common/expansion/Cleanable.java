/*
 * PlaceholderAPI
 * Copyright (C) 2019 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.clip.placeholderapi.common.expansion;

import me.clip.placeholderapi.common.PAPIPlayer;

/**
 * This interface allows a class which extends a PlaceholderExpansion to have the cleanup
 * method called every time a player leaves the server. This is useful if we want to clean up after
 * the player
 *
 * @author Ryan McCarthy
 */
public interface Cleanable {
    /**
     * Called when a player leaves the server
     *
     * @param p {@link PAPIPlayer} who left the server
     */
    void cleanup(PAPIPlayer p);
}
