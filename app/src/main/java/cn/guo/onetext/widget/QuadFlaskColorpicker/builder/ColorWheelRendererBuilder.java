package cn.guo.onetext.widget.QuadFlaskColorpicker.builder;

import cn.guo.onetext.widget.QuadFlaskColorpicker.ColorPickerView;
import cn.guo.onetext.widget.QuadFlaskColorpicker.renderer.ColorWheelRenderer;
import cn.guo.onetext.widget.QuadFlaskColorpicker.renderer.FlowerColorWheelRenderer;
import cn.guo.onetext.widget.QuadFlaskColorpicker.renderer.SimpleColorWheelRenderer;

public class ColorWheelRendererBuilder {
    public static ColorWheelRenderer getRenderer(ColorPickerView.WHEEL_TYPE wheelType) {
        switch (wheelType) {
            case CIRCLE:
                return new SimpleColorWheelRenderer();
            case FLOWER:
                return new FlowerColorWheelRenderer();
        }
        throw new IllegalArgumentException("wrong WHEEL_TYPE");
    }
}