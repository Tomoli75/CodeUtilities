/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.codeutilities.commands.impl.schem.sys.sk89q.jnbt;

/**
 * A tag that has a name.
 */
public class NamedTag {

    private final String name;
    private final Tag tag;

    /**
     * Create a new named tag.
     *
     * @param name the name
     * @param tag  the tag
     */
    public NamedTag(String name, Tag tag) {
        if (name == null)
            throw new NullPointerException();

        if (tag == null)
            throw new NullPointerException();

        this.name = name;
        this.tag = tag;
    }

    /**
     * Get the name of the tag.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the tag.
     *
     * @return the tag
     */
    public Tag getTag() {
        return tag;
    }

}
