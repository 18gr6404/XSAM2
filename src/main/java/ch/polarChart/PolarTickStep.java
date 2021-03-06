/*
 * Copyright (c) 2018 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.polarChart;

public enum PolarTickStep {
    FIVE(5), FIFTEEN(15), FOURTY_FIVE(45), NINETY(90);

    private final double VALUE;

    PolarTickStep(final double VALUE) {
        this.VALUE = VALUE;
    }

    public double get() { return VALUE; }
}
