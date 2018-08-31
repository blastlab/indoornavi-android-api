package co.blastlab.indoornavi_api.model;

import android.support.annotation.ColorInt;

/**
 * Class representing a Border.
 */
public class Border {
	public int width;
	public @ColorInt int color;

	/**
	 * Border object
	 *
	 * @param width width of the border
	 * @param color color of the border
	 */
	public Border(int width, @ColorInt int color) {
		this.width = width;
		this.color = color;
	}
}
