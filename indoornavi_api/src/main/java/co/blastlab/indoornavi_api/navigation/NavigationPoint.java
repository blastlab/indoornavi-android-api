package co.blastlab.indoornavi_api.navigation;

import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;

import co.blastlab.indoornavi_api.model.Border;

public class NavigationPoint {

	private int radius;
	private @ColorInt int color;
	private @FloatRange(from = 0.0, to = 1.0) double opacity;
	private Border border;


	private NavigationPoint(){};

	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * @return radius of the circle. Return Integer value.
	 */
	public int getRadius() {
		return this.radius;
	}

	/**
	 * Sets color of the circle.
	 *
	 * @param color that specifies the color.
	 */
	public void setColor(@ColorInt int color) {
		this.color = color;
	}

	/**
	 * @return color of the circle represent as an Integer.
	 */
	public @ColorInt int getColor() {
		return this.color;
	}

	/**
	 * Sets opacity of the circle.
	 *
	 * @param opacity Float between 1.0 and 0. Set it to 1.0 for no opacity, 0 for maximum opacity.
	 */
	public void setOpacity(@FloatRange(from = 0.0, to = 1.0) double opacity) {
		this.opacity = opacity;
	}

	/**
	 * @return opacity of the circle. Return Float value.
	 */
	@FloatRange(from = 0.0, to = 1.0) public  double getOpacity() {
		return this.opacity;
	}

	/**
	 * Sets Border of the circle.
	 *
	 * @param border Border object, define border parameters of the circle.
	 */
	public void setBorder(Border border) {
		this.border = border;
	}

	/**
	 * Gets border of the circle.
	 */
	public Border getBorder() {
		return this.border;
	}

	public static class NavigationPointBuilder {

		private NavigationPoint navigationPoint;

		public NavigationPointBuilder() {
			navigationPoint = new NavigationPoint();
		}

		public NavigationPointBuilder setRadius(int radius) {
			navigationPoint.setRadius(radius);
			return this;
		}

		public NavigationPointBuilder setColor(@ColorInt int color) {
			navigationPoint.setColor(color);
			return this;
		}

		public NavigationPointBuilder setOpacity(@FloatRange(from = 0.0, to = 1.0) double opacity) {
			navigationPoint.setOpacity(opacity);
			return this;
		}

		public NavigationPointBuilder setBorder(Border border) {
			navigationPoint.setBorder(border);
			return this;
		}

		public NavigationPoint build() {
			return navigationPoint;
		}
	}
}
