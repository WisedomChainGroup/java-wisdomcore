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

package org.wisdom.service;

public interface HatchService {

    Object getBalance(String pubkeyhash);

    Object getNonce(String pubkeyhash);

    Object getTransfer(int height);

    Object getHatch(int height);

    Object getInterest(int height);

    Object getShare(int height);

    Object getCost(int height);

    Object getVote(int height);

    Object getCancelVote(int height);

    Object getMortgage(int height);

    Object getCancelMortgage(int height);

    Object getNowInterest(String tranhash);

    Object getNowShare(String tranhash);

    Object getTxrecordFromAddress(String address);

    Object getCoinBaseList(int height);
}