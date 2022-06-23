package cn.guo.onetext.widget.QuadFlaskColorpicker.renderer;

import java.util.List;

import cn.guo.onetext.widget.QuadFlaskColorpicker.ColorCircle;

public interface ColorWheelRenderer {
    float GAP_PERCENTAGE = 0.025f;

    void draw();

    ColorWheelRenderOption getRenderOption();

    void initWith(ColorWheelRenderOption colorWheelRenderOption);

    List<ColorCircle> getColorCircleList();
}