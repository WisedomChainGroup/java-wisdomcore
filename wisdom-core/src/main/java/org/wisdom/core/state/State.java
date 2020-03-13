/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.core.state;

import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.List;

@Deprecated
public interface State<T> {
    // transition and return self
    T updateBlock(Block block);

    // transition blocks and return self
    T updateBlocks(List<Block> blocks);

    // transition transaction and return self
    T updateTransaction(Transaction transaction);

    // deep copy
    T copy();
}