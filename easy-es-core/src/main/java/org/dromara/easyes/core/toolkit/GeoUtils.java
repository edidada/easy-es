package org.dromara.easyes.core.toolkit;

import co.elastic.clients.elasticsearch._types.GeoLocation;
import org.dromara.easyes.common.exception.EasyEsException;
import org.elasticsearch.geometry.Geometry;
import org.elasticsearch.geometry.Point;
import org.elasticsearch.geometry.Rectangle;
import org.elasticsearch.geometry.ShapeType;
import org.elasticsearch.geometry.utils.BitUtil;
import org.elasticsearch.geometry.utils.GeographyValidator;
import org.elasticsearch.geometry.utils.Geohash;
import org.elasticsearch.geometry.utils.WellKnownText;

import java.util.Locale;

/**
 * geo工具类
 *
 * @author jaime
 * @version 1.0
 * @since 2025/2/18
 */
public class GeoUtils {

    /**
     * 读取geohash或lat-lon组合
     *
     * @param value 此 String 必须是 geohash 或 lat-lon组合(以逗号间隔)
     * @return GeoLocation
     */
    public static GeoLocation create(String value) {
        return create(value, false, EffectivePoint.BOTTOM_LEFT);
    }

    public static GeoLocation create(String value, final boolean ignoreZValue, EffectivePoint effectivePoint) {
        if (value.toLowerCase(Locale.ROOT).contains("point")) {
            return createFromWKT(value, ignoreZValue);
        } else if (value.contains(",")) {
            return createFromCoordinates(value, ignoreZValue);
        }
        return parseGeoHash(value, effectivePoint);
    }

    public static GeoLocation createFromCoordinates(String value, final boolean ignoreZValue) {
        String[] vals = value.split(",");
        if (vals.length > 3) {
            throw new EasyEsException(String.format("failed to parse [%s], expected 2 or 3 coordinates but found: [%s]", value, vals.length));
        }
        final double lat;
        final double lon;
        try {
            lat = Double.parseDouble(vals[0].trim());
        } catch (NumberFormatException ex) {
            throw new EasyEsException("latitude must be a number");
        }
        try {
            lon = Double.parseDouble(vals[1].trim());
        } catch (NumberFormatException ex) {
            throw new EasyEsException("longitude must be a number");
        }
        if (vals.length > 2 && !ignoreZValue) {
            throw new EasyEsException(String.format(
                    "Exception parsing coordinates: found Z value [%s] but [ignore_z_value] parameter is [%s]",
                    Double.parseDouble(vals[2].trim()), ignoreZValue));
        }
        return create(lat, lon);
    }

    private static GeoLocation createFromWKT(String value, boolean ignoreZValue) {
        Geometry geometry;
        try {
            geometry = WellKnownText.fromWKT(GeographyValidator.instance(ignoreZValue), false, value);
        } catch (Exception e) {
            throw new EasyEsException("Invalid WKT format", e);
        }
        if (geometry.type() != ShapeType.POINT) {
            throw new EasyEsException(
                    "[geo_point] supports only POINT among WKT primitives, " + "but found " + geometry.type()
            );
        }
        Point point = (Point) geometry;
        return create(point.getY(), point.getX());
    }

    public static GeoLocation parseGeoHash(String geohash, EffectivePoint effectivePoint) {
        if (effectivePoint == EffectivePoint.BOTTOM_LEFT) {
            return createFromGeoHash(geohash);
        } else {
            Rectangle rectangle = Geohash.toBoundingBox(geohash);
            return switch (effectivePoint) {
                case TOP_LEFT -> create(rectangle.getMaxY(), rectangle.getMinX());
                case TOP_RIGHT -> create(rectangle.getMaxY(), rectangle.getMaxX());
                case BOTTOM_RIGHT -> create(rectangle.getMinY(), rectangle.getMaxX());
                default -> throw new IllegalArgumentException("Unsupported effective point " + effectivePoint);
            };
        }
    }

    public static GeoLocation createFromIndexHash(long hash) {
        return create(Geohash.decodeLatitude(hash), Geohash.decodeLongitude(hash));
    }

    public static GeoLocation createFromGeoHash(String geohash) {
        return createFromIndexHash(Geohash.mortonEncode(geohash));
    }

    public static GeoLocation createFromGeoHash(long geohashLong) {
        final int level = (int) (12 - (geohashLong & 15));
        return createFromIndexHash(BitUtil.flipFlop((geohashLong >>> 4) << ((level * 5) + 2)));
    }

    public static GeoLocation create(double lat, double lon) {
        return GeoLocation.of(b -> b.latlon(x -> x.lat(lat).lon(lon)));
    }

    /**
     * 表示 geohash 单元格中应用作 geohash 值的点
     */
    public enum EffectivePoint {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}
