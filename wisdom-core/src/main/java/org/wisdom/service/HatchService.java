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

    Object getTransfer(long height);

    Object getHatch(long height);

    Object getInterest(long height);

    Object getShare(long height);

    Object getCost(long height);

    Object getVote(long height);

    Object getCancelVote(long height);

    Object getMortgage(long height);

    Object getCancelMortgage(long height);

    Object getNowInterest(String tranhash);

    Object getNowShare(String tranhash);

    @Deprecated
    Object getTxrecordFromAddress(String address);

    Object getCoinBaseList(long height);

    Object getAssetList(long height);

    Object getAssetTransferList(long height);

    Object getAssetOwnerList(long height);

    Object getAssetIncreasedList(long height);
}