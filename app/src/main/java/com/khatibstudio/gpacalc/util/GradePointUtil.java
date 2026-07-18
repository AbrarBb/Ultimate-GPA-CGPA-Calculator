package com.khatibstudio.gpacalc.util;

import com.khatibstudio.gpacalc.data.entity.GradePoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GradePointUtil {

    private GradePointUtil() {
    }

    public static Map<String, GradePoint> toMap(List<GradePoint> points) {
        Map<String, GradePoint> map = new HashMap<>();
        if (points == null) return map;
        for (GradePoint gp : points) {
            map.put(gp.letterGrade, gp);
        }
        return map;
    }

    public static String[] gradeLabels(List<GradePoint> points) {
        if (points == null || points.isEmpty()) return new String[0];
        String[] labels = new String[points.size()];
        for (int i = 0; i < points.size(); i++) {
            GradePoint gp = points.get(i);
            labels[i] = gp.letterGrade + " (" + String.format("%.2f", gp.pointValue) + ")";
        }
        return labels;
    }

    public static String extractGrade(String label) {
        if (label == null) return "";
        int idx = label.indexOf(' ');
        return idx > 0 ? label.substring(0, idx) : label.trim();
    }
}
