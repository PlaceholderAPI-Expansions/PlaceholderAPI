/*
 *
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
 *
 *
 */
package me.clip.placeholderapi.expansion;

public final class Version {

  private final boolean isSpigot;
  private final String version;

  public Version(String version, boolean isSpigot) {
    this.version = version;
    this.isSpigot = isSpigot;
  }

  public String getVersion() {
    return version == null ? "unknown" : version;
  }

  public boolean isSpigot() {
    return isSpigot;
  }

  public boolean compareTo(String version) {
    return getVersion().equalsIgnoreCase(version);
  }
}
