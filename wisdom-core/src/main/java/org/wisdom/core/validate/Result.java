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

package org.wisdom.core.validate;

public class Result {
    public static final Result SUCCESS = new Result(true, null);

    public static Result Error(String msg){
        return new Result(false, msg);
    }
    private boolean success;
    private String message;

    private Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess(){
        return success;
    }

    public String getMessage(){
        return message;
    }
}