/**
 * Wire
 * Copyright (C) 2017 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.paintcode;

import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Canvas;
import android.graphics.Path;


/**
 * Created by Wire Design Team on 8. Dec 2017.
 * Copyright © 2017 Wire Swiss GmbH. All rights reserved.
 *
 * Generated by PaintCode
 * http://www.paintcodeapp.com
 *
 * @author Wire Design Team
 */
public class AvailabilityIcons {

    // Resizing Behavior
    public enum ResizingBehavior {
        AspectFit, //!< The content is proportionally resized to fit into the target rectangle.
        AspectFill, //!< The content is proportionally resized to completely fill the target rectangle.
        Stretch, //!< The content is stretched to match the entire target rectangle.
        Center, //!< The content is centered in the target rectangle, but it is NOT resized.
    }

    // Canvas Drawings
    // Tab

    private static class CacheForAvailableIcon {
        private static Paint paint = new Paint();
        private static RectF originalFrame = new RectF(0f, 0f, 64f, 64f);
        private static RectF resizedFrame = new RectF();
        private static RectF bezierRect = new RectF();
        private static Path bezierPath = new Path();
    }

    public static void drawAvailableIcon(Canvas canvas, int fillColor) {
        AvailabilityIcons.drawAvailableIcon(canvas, new RectF(0f, 0f, 64f, 64f), ResizingBehavior.AspectFit, fillColor);
    }

    public static void drawAvailableIcon(Canvas canvas, RectF targetFrame, ResizingBehavior resizing, int fillColor) {
        // General Declarations
        Paint paint = CacheForAvailableIcon.paint;

        // Resize to Target Frame
        canvas.save();
        RectF resizedFrame = CacheForAvailableIcon.resizedFrame;
        AvailabilityIcons.resizingBehaviorApply(resizing, CacheForAvailableIcon.originalFrame, targetFrame, resizedFrame);
        canvas.translate(resizedFrame.left, resizedFrame.top);
        canvas.scale(resizedFrame.width() / 64f, resizedFrame.height() / 64f);

        // Bezier
        RectF bezierRect = CacheForAvailableIcon.bezierRect;
        bezierRect.set(0f, 0f, 64f, 64f);
        Path bezierPath = CacheForAvailableIcon.bezierPath;
        bezierPath.reset();
        bezierPath.moveTo(32f, 64f);
        bezierPath.cubicTo(49.67f, 64f, 64f, 49.67f, 64f, 32f);
        bezierPath.cubicTo(64f, 14.33f, 49.67f, 0f, 32f, 0f);
        bezierPath.cubicTo(14.33f, 0f, 0f, 14.33f, 0f, 32f);
        bezierPath.cubicTo(0f, 49.67f, 14.33f, 64f, 32f, 64f);
        bezierPath.close();

        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        bezierPath.setFillType(Path.FillType.EVEN_ODD);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fillColor);
        canvas.drawPath(bezierPath, paint);

        canvas.restore();
    }

    private static class CacheForAwayIcon {
        private static Paint paint = new Paint();
        private static RectF originalFrame = new RectF(0f, 0f, 64f, 64f);
        private static RectF resizedFrame = new RectF();
        private static RectF bezierRect = new RectF();
        private static Path bezierPath = new Path();
    }

    public static void drawAwayIcon(Canvas canvas, int fillColor) {
        AvailabilityIcons.drawAwayIcon(canvas, new RectF(0f, 0f, 64f, 64f), ResizingBehavior.AspectFit, fillColor);
    }

    public static void drawAwayIcon(Canvas canvas, RectF targetFrame, ResizingBehavior resizing, int fillColor) {
        // General Declarations
        Paint paint = CacheForAwayIcon.paint;

        // Resize to Target Frame
        canvas.save();
        RectF resizedFrame = CacheForAwayIcon.resizedFrame;
        AvailabilityIcons.resizingBehaviorApply(resizing, CacheForAwayIcon.originalFrame, targetFrame, resizedFrame);
        canvas.translate(resizedFrame.left, resizedFrame.top);
        canvas.scale(resizedFrame.width() / 64f, resizedFrame.height() / 64f);

        // Bezier
        RectF bezierRect = CacheForAwayIcon.bezierRect;
        bezierRect.set(0f, 0f, 64f, 64f);
        Path bezierPath = CacheForAwayIcon.bezierPath;
        bezierPath.reset();
        bezierPath.moveTo(32f, 64f);
        bezierPath.cubicTo(14.33f, 64f, 0f, 49.67f, 0f, 32f);
        bezierPath.cubicTo(0f, 14.33f, 14.33f, 0f, 32f, 0f);
        bezierPath.cubicTo(49.67f, 0f, 64f, 14.33f, 64f, 32f);
        bezierPath.cubicTo(64f, 49.67f, 49.67f, 64f, 32f, 64f);
        bezierPath.close();
        bezierPath.moveTo(32f, 51.2f);
        bezierPath.cubicTo(42.6f, 51.2f, 51.2f, 42.6f, 51.2f, 32f);
        bezierPath.cubicTo(51.2f, 21.4f, 42.6f, 12.8f, 32f, 12.8f);
        bezierPath.cubicTo(21.4f, 12.8f, 12.8f, 21.4f, 12.8f, 32f);
        bezierPath.cubicTo(12.8f, 42.6f, 21.4f, 51.2f, 32f, 51.2f);
        bezierPath.close();

        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        bezierPath.setFillType(Path.FillType.EVEN_ODD);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fillColor);
        canvas.drawPath(bezierPath, paint);

        canvas.restore();
    }

    private static class CacheForBusyIcon {
        private static Paint paint = new Paint();
        private static RectF originalFrame = new RectF(0f, 0f, 64f, 64f);
        private static RectF resizedFrame = new RectF();
        private static RectF bezierRect = new RectF();
        private static Path bezierPath = new Path();
    }

    public static void drawBusyIcon(Canvas canvas, int fillColor) {
        AvailabilityIcons.drawBusyIcon(canvas, new RectF(0f, 0f, 64f, 64f), ResizingBehavior.AspectFit, fillColor);
    }

    public static void drawBusyIcon(Canvas canvas, RectF targetFrame, ResizingBehavior resizing, int fillColor) {
        // General Declarations
        Paint paint = CacheForBusyIcon.paint;

        // Resize to Target Frame
        canvas.save();
        RectF resizedFrame = CacheForBusyIcon.resizedFrame;
        AvailabilityIcons.resizingBehaviorApply(resizing, CacheForBusyIcon.originalFrame, targetFrame, resizedFrame);
        canvas.translate(resizedFrame.left, resizedFrame.top);
        canvas.scale(resizedFrame.width() / 64f, resizedFrame.height() / 64f);

        // Bezier
        RectF bezierRect = CacheForBusyIcon.bezierRect;
        bezierRect.set(0f, 0f, 64f, 64f);
        Path bezierPath = CacheForBusyIcon.bezierPath;
        bezierPath.reset();
        bezierPath.moveTo(32f, 64f);
        bezierPath.cubicTo(14.33f, 64f, 0f, 49.67f, 0f, 32f);
        bezierPath.cubicTo(0f, 14.33f, 14.33f, 0f, 32f, 0f);
        bezierPath.cubicTo(49.67f, 0f, 64f, 14.33f, 64f, 32f);
        bezierPath.cubicTo(64f, 49.67f, 49.67f, 64f, 32f, 64f);
        bezierPath.close();
        bezierPath.moveTo(12.8f, 25.6f);
        bezierPath.lineTo(12.8f, 38.4f);
        bezierPath.lineTo(51.2f, 38.4f);
        bezierPath.lineTo(51.2f, 25.6f);
        bezierPath.lineTo(12.8f, 25.6f);
        bezierPath.close();

        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        bezierPath.setFillType(Path.FillType.EVEN_ODD);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(fillColor);
        canvas.drawPath(bezierPath, paint);

        canvas.restore();
    }


    // Canvas Images
    // Tab


    // Resizing Behavior
    public static void resizingBehaviorApply(ResizingBehavior behavior, RectF rect, RectF target, RectF result) {
        if (rect.equals(target) || target == null) {
            result.set(rect);
            return;
        }

        if (behavior == ResizingBehavior.Stretch) {
            result.set(target);
            return;
        }

        float xRatio = Math.abs(target.width() / rect.width());
        float yRatio = Math.abs(target.height() / rect.height());
        float scale = 0f;

        switch (behavior) {
            case AspectFit: {
                scale = Math.min(xRatio, yRatio);
                break;
            }
            case AspectFill: {
                scale = Math.max(xRatio, yRatio);
                break;
            }
            case Center: {
                scale = 1f;
                break;
            }
        }

        float newWidth = Math.abs(rect.width() * scale);
        float newHeight = Math.abs(rect.height() * scale);
        result.set(target.centerX() - newWidth / 2,
            target.centerY() - newHeight / 2,
            target.centerX() + newWidth / 2,
            target.centerY() + newHeight / 2);
    }


}
