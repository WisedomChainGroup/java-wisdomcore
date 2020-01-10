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

package org.wisdom.core;

import org.wisdom.core.event.NewBestBlockEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.wisdom.pool.PeningTransPool;

@Component
public class StatetreeUpdate implements ApplicationListener<NewBestBlockEvent> {

    @Autowired
    PeningTransPool peningTransPool;

    @Override
    public void onApplicationEvent(NewBestBlockEvent event) {
        Block b = event.getBlock();
        peningTransPool.updatePool(b.body, 2, b.nHeight);
    }
}