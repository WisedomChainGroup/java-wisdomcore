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

package org.wisdom.consensus.pow;

public class EconomicModel {
    public static final long WDC = 100000000;

    public static final long INITIAL_SUPPLY = 20 * WDC;

    public static final long HALF_PERIOD = 1051200 * 2;

    public static long getConsensusRewardAtHeight(long height){
        long era = height / HALF_PERIOD;
        long reward = INITIAL_SUPPLY;
        for(long i = 0; i < era; i++){
            reward = reward * 52218182 / 100000000;
        }
        return reward;
    }

    public static void printRewardPerEra(){
        for(long reward = INITIAL_SUPPLY; reward > 0; reward = reward * 52218182 / 100000000){
            System.out.println(reward * 1.0 / WDC );
        }
    }

    // 9140868887284800
    public static long getTotalSupply(){
        long totalSupply = 0;
        for(long i = 0; ; i++){
            long reward = getConsensusRewardAtHeight(i);
            if(reward == 0){
                return totalSupply;
            }
            totalSupply += reward;
        }
    }

    public static void main(String[] args){
        System.out.println(getTotalSupply());
    }
}