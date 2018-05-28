/*
 * Copyright (c) 2017 by Gerrit Grunwald
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


import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by hansolo on 16.07.17.
 */
public class XYChartItem implements XYItem, Comparable<XYChartItem> {
    private final ItemEvent                         ITEM_EVENT = new ItemEvent(XYChartItem.this);
    private double                                  _x;
    private DoubleProperty                          x;
    private double                                  _y;
    private DoubleProperty                          y;
    private String                                  _name;
    private StringProperty                          name;
    private Color                                   _fill;
    private ObjectProperty<Color>                   fill;
    private Color                                   _stroke;
    private ObjectProperty<Color>                   stroke;
    private Symbol                                  _symbol;
    private ObjectProperty<Symbol>                  symbol;


    // ******************** Constructors **********************************
    public XYChartItem() {
        this(0, 0, "", Color.RED, Color.TRANSPARENT, Symbol.NONE);
    }
    public XYChartItem(final double X, final double Y) {
        this(X, Y, "", Color.RED, Color.TRANSPARENT, Symbol.NONE);
    }
    public XYChartItem(final double X, final double Y, final Color FILL) {
        this(X, Y, "", FILL, Color.TRANSPARENT, Symbol.NONE);
    }
    public XYChartItem(final double X, final double Y, final String NAME) {
        this(X, Y, NAME, Color.RED, Color.TRANSPARENT, Symbol.NONE);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final Color FILL) {
        this(X, Y, NAME, FILL, Color.TRANSPARENT, Symbol.NONE);
    }
    public XYChartItem(final double X, final double Y, final String NAME, final Color FILL, final Color STROKE, final Symbol SYMBOL) {
        _x        = X;
        _y        = Y;
        _name     = NAME;
        _fill     = FILL;
        _stroke   = STROKE;
        _symbol   = SYMBOL;
    }


    // ******************** Methods ***************************************
    @Override public double getX() { return null == x ? _x : x.get(); }
    @Override public void setX(final double X) {
        if (null == x) {
            _x = X;
        } else {
            x.set(X);
        }
    }
    @Override public DoubleProperty xProperty() {
        if (null == x) {
            x = new DoublePropertyBase(_x) {
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "x"; }
            };
        }
        return x;
    }

    @Override public double getY() { return null == y ? _y : y.get(); }
    @Override public void setY(final double Y) {
        if (null == y) {
            _y = Y;
        } else {
            y.set(Y);
        }
    }
    @Override public DoubleProperty yProperty() {
        if (null == y) {
            y = new DoublePropertyBase(_y) {
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "y"; }
            };
        }
        return y;
    }

    @Override public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    @Override public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color FILL) {
        if (null == fill) {
            _fill = FILL;
        } else {
            fill.set(FILL);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill = new ObjectPropertyBase<Color>(_fill) {
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }

    @Override public Color getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Color STROKE) {
        if (null == stroke) {
            _stroke = STROKE;
        } else {
            stroke.set(STROKE);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<Color>(_stroke) {
                @Override public Object getBean() { return XYChartItem.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    @Override public Symbol getSymbol() { return null == symbol ? _symbol : symbol.get(); }
    public void setSymbol(final Symbol SYMBOL) {
        if (null == symbol) {
            _symbol = SYMBOL;
        } else {
            symbol.set(SYMBOL);
        }
    }
    public ObjectProperty<Symbol> symbolProperty() {
        if (null == symbol) {
            symbol = new ObjectPropertyBase<Symbol>(_symbol) {
                @Override public Object getBean() {  return XYChartItem.this;  }
                @Override public String getName() {  return "symbol";  }
            };
            _symbol = null;
        }
        return symbol;
    }


    @Override public int compareTo(final XYChartItem ITEM) { return Double.compare(getX(), ITEM.getX()); }
}
