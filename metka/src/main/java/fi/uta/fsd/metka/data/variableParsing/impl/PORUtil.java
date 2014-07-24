package fi.uta.fsd.metka.data.variableParsing.impl;

import spssio.por.*;
import spssio.util.NumberParser;
import spssio.util.NumberSystem;

import java.util.*;

class PORUtil {
    private static NumberParser parser = new NumberParser(new NumberSystem(30, null));
    public static Double parseDouble(String value) throws NumberFormatException {
        parser.reset();
        double result = parser.parseDouble(value);
        int errno = parser.errno();
        if(result == NumberParser.DBL_ERRVALUE && errno != NumberParser.E_OK) {
            throw new NumberFormatException("DBL_ERRVALUE returned. Error: "+errno);
        }
        return result;
    }

    static void groupVariables(List<PORVariableHolder> list, Vector<PORVariable> variables, Vector<PORValueLabels> labels) {
        for(PORVariable variable : variables) {
            list.add(new PORVariableHolder(variable));
        }

        for(PORValueLabels vl : labels) {
            for(PORVariable variable : vl.vars) {
                list.get(list.indexOf(new PORVariableHolder(variable))).addLabels(vl);
            }
        }
    }

    static class PORVariableHolder {
        private final PORVariable var;
        private final List<PORMissingValueHolder> missing;
        private final List<PORVariableValueLabel> labels;
        private final List<PORVariableData> data;

        public PORVariableHolder(PORVariable var) {
            this.var = var;
            missing = new ArrayList<>();
            labels = new ArrayList<>();
            data = new ArrayList<>();

            setMissing();
        }

        private void setMissing() {
            for(PORMissingValue m : var.missvalues) {
                PORMissingValueHolder holder = null;
                switch(m.type) {
                    case PORMissingValue.TYPE_UNASSIGNED:
                        // Can't do anything, missing value is unfinished
                        break;
                    case PORMissingValue.TYPE_DISCRETE_VALUE:
                        // There has to be exactly one missing value and the type must not be unassigned.
                        if(m.values.length == 1 && m.values[0] != null && m.values[0].type != PORValue.TYPE_UNASSIGNED) {
                            PORValue v = m.values[0];
                            holder = new PORMissingDiscreteValue((v.type == PORValue.TYPE_NUMERIC), v.value);
                        }
                        break;
                    case PORMissingValue.TYPE_RANGE_OPEN_LO:
                        // There has to be exactly one missing value and the type should be numeric. We can't deal with ranges in String so those are ignored
                        if(m.values.length == 1 && m.values[0] != null && m.values[0].type == PORValue.TYPE_NUMERIC) {
                            holder = new PORMissingOpenLowRange(m.values[0].value);
                        }
                        break;
                    case PORMissingValue.TYPE_RANGE_OPEN_HI:
                        // There has to be exactly one missing value and the type should be numeric. We can't deal with ranges in String so those are ignored
                        if(m.values.length == 1 && m.values[0] != null && m.values[0].type == PORValue.TYPE_NUMERIC) {
                            holder = new PORMissingOpenHighRange(m.values[0].value);
                        }
                        break;
                    case PORMissingValue.TYPE_RANGE_CLOSED:
                        // There has to be exactly two missing values and both have to be of type numeric. We can't deal with ranges in String so those are ignored.
                        if(m.values.length == 2 && m.values[0] != null && m.values[0].type == PORValue.TYPE_NUMERIC && m.values[1] != null && m.values[1].type == PORValue.TYPE_NUMERIC) {
                            holder = new PORMissingClosedRange(m.values[0].value, m.values[1].value);
                        }
                        break;
                }
                if(holder != null) {
                    if(holder.isRange()) {
                        if(((PORMissingRangeValue)holder).isValid()) {
                            missing.add(holder);
                        }
                    } else {
                        missing.add(holder);
                    }
                }
            }
        }

        public PORVariable asVariable() {
            return var;
        }

        List<PORMissingValueHolder> getMissing() {
            List<PORMissingValueHolder> copy = new ArrayList<>(missing);
            return copy;
        }

        public int getMissingSize() {
            return missing.size();
        }

        public List<PORVariableValueLabel> getLabels() {
            List<PORVariableValueLabel> copy = new ArrayList<>(labels);
            return copy;
        }

        public int getLabelsSize() {
            return labels.size();
        }

        public List<PORVariableData> getData() {
            List<PORVariableData> copy = new ArrayList<>(data);
            return copy;
        }

        public int getDataSize() {
            return data.size();
        }

        /**
         * Return all numerical data (not SYSMISS or String) that doesn't match a user missing value.
         * @return List of all valid numerical data, mainly used for statistics
         */
        public List<PORVariableDataNumeric> getValidNumericalData() {
            List<PORVariableDataNumeric> copy = new ArrayList<>();
            for(PORVariableData d : data) {
                if(d.isNumerical() && !isUserMissing(((PORVariableDataNumeric)d).getValue())) {
                    copy.add((PORVariableDataNumeric)d);
                }
            }
            return copy;
        }

        /**
         * Checks given PORValue against all present user missing values.
         * @param value
         * @return
         */
        public boolean isUserMissing(PORValue value) {
            for(PORMissingValueHolder m : getMissing()) {
                switch(m.getType()) {
                    case DISCRETE:
                        if(((PORMissingDiscreteValue)m).valueIsMissing(value)) {
                            return true;
                        }
                        break;
                    case OPEN_LO:
                    case OPEN_HI:
                    case RANGE_CLOSED:
                        if(((PORMissingRangeValue)m).withinRange(value)) {
                            return true;
                        }
                        break;
                    case UNASSIGNED:
                        break;
                }
            }
            return false;
        }

        /**
         * Checks given string value against all present user missing values.
         * Only string type discrete values can match a string value. In all other cases nothing is returned
         * @param value
         * @return
         */
        public boolean isUserMissing(String value) {
            for(PORMissingValueHolder m : getMissing()) {
                if(m.getType() == PORMissingValueHolder.PORMissingType.DISCRETE) {
                    if(((PORMissingDiscreteValue)m).valueIsMissing(value)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Checks given double against all present user missing values.
         * String type discrete missing values will give false by default but all other cases are checked.
         * @param value
         * @return
         */
        public boolean isUserMissing(Double value) {
            for(PORMissingValueHolder m : getMissing()) {
                switch(m.getType()) {
                    case DISCRETE:
                        if(((PORMissingDiscreteValue)m).valueIsMissing(value)) {
                            return true;
                        }
                        break;
                    case OPEN_LO:
                    case OPEN_HI:
                    case RANGE_CLOSED:
                        if(((PORMissingRangeValue)m).withinRange(value)) {
                            return true;
                        }
                        break;
                    case UNASSIGNED:
                        break;
                }
            }
            return false;
        }

        public void addLabels(PORValueLabels labels) {
            for(PORValue key : labels.mappings.keySet()) {
                PORVariableValueLabel valLbl = new PORVariableValueLabel(labels.mappings.get(key), key, isUserMissing(key));
                this.labels.add(valLbl);
            }
        }

        public boolean hasLabels() {return labels.size() > 0;}

        public void addMissing() {
            data.add(new PORVariableDataMissing());
        }

        /*public void addInt(int i) {
            data.add(new PORVariableDataInt(i));
        }

        public void addDouble(double d) {
            data.add(new PORVariableDataDouble(d));
        }*/

        public void addNumeric(double d) {
            data.add(new PORVariableDataNumeric(d));
        }

        public void addString(String s) {
            data.add(new PORVariableDataString(s));
        }

        public int valueCount(String value) {
            int count = 0;
            for(PORVariableData varD : data) {
                if(varD.toString().equals(value)) {
                    count++;
                }
            }

            return count;
        }

        public boolean isNumeric() {
            if(var.width == 0) {
                return true;
            } else {
                return false;
            }
        }

        public PORVariableValueLabel getLabel(String value) {
            for(PORVariableValueLabel label : labels) {
                if(label.getValue().equals(value)) {
                    return label;
                }
            }
            return null;
        }

        @Override
        public int hashCode() {
            return var.name.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PORVariableHolder that = (PORVariableHolder) o;

            if (!var.name.equals(that.var.name)) return false;

            return true;
        }
    }

    static abstract class PORMissingValueHolder {
        static enum PORMissingType {
            UNASSIGNED,
            OPEN_HI,
            OPEN_LO,
            DISCRETE,
            RANGE_CLOSED
        }

        private final PORMissingType type;
        private final boolean numeric;
        private final boolean range;

        PORMissingValueHolder(PORMissingType type, boolean numeric, boolean range) {
            this.type = type;
            this.numeric = numeric;
            this.range = range;
        }

        PORMissingType getType() {
            return type;
        }

        boolean isNumeric() {
            return numeric;
        }

        boolean isRange() {
            return range;
        }

        protected Double asDouble(String value) throws NumberFormatException {
            if(isNumeric()) {
                return PORUtil.parseDouble(value);
            } else {
                throw new NumberFormatException();
            }
        }

        protected boolean isInteger(Double dValue) {
            if(isNumeric()) {
                int i = dValue.intValue();
                if(dValue.equals(new Double(i))) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        protected Integer asInteger(Double dValue) throws NumberFormatException {
            if(isNumeric()) {
                return dValue.intValue();
            } else {
                throw new NumberFormatException();
            }
        }
    }

    static class PORMissingDiscreteValue extends PORMissingValueHolder {
        private final String value;
        private final Double dValue;

        protected PORMissingDiscreteValue(boolean numeric, String value) {
            super(PORMissingType.DISCRETE, numeric, false);
            this.value = value;
            if(numeric) {
                dValue = asDouble(value);
            } else {
                dValue = Double.NaN;
            }
        }

        String getValue() {
            return value;
        }

        Double asDouble() {
            return dValue;
        }

        boolean isInteger() {
            return super.isInteger(dValue);
        }

        Integer asInteger() {
            return super.asInteger(dValue);
        }

        boolean valueIsMissing(PORValue value) throws NumberFormatException {
            if(isNumeric() && (value.type == PORValue.TYPE_NUMERIC)) {
                Double d = asDouble(value.value);
                return valueIsMissing(d);
            } else if(!isNumeric() && (value.type == PORValue.TYPE_STRING)) {
                return valueIsMissing(value.value);
            } else {
                return false;
            }
        }

        boolean valueIsMissing(String value) {
            if(isNumeric()) {
                return false;
            }
            return this.value.equals(value);
        }

        boolean valueIsMissing(Double value) {
            if(!isNumeric()) {
                return false;
            }
            return (dValue.equals(value));
        }
    }

    static abstract class PORMissingSingleValueRange extends PORMissingValueHolder {
        private final Double dValue;

        protected PORMissingSingleValueRange(PORMissingType type, String value) {
            super(type, true, true);
            Double d = asDouble(value);
            // Set dValue taking into consideration the distinct cases of 0.0d and -0.0d.
            // This enables clear range comparisons
            if(d.equals((type == PORMissingType.OPEN_LO) ? 0.0d : -0.0d)) {
                dValue = (type == PORMissingType.OPEN_LO) ? -0.0d : 0.0d;
            } else {
                dValue = d;
            }
        }

        Double getValue() {
            return dValue;
        }

        boolean isInteger() {
            return super.isInteger(dValue);
        }

        Integer asInteger() {
            return super.asInteger(dValue);
        }
    }

    static interface PORMissingRangeValue {
        /**
         * Checks to see that range value is a valid value for comparison.
         * If range includes a NaN value it should be discarded.
         * @return boolean False if any value in the range equals NaN.
         */
        boolean isValid();

        /**
         * Checks if given PORValue is within range of this range missing value.
         * PORValue has to be numeric for comparison to take place.
         * @param value PORValue to check for being within this range
         * @return boolean telling if given PORValue is within this range
         * @throws NumberFormatException
         */
        boolean withinRange(PORValue value) throws NumberFormatException;

        /**
         * Checks if given value is within range of this ranged missing value
         * @param value String representation of value to be checked. Assumed to be base30 representation
         * @return boolean telling if given value is within this range
         * @throws NumberFormatException
         */
        boolean withinRange(String value) throws NumberFormatException;

        /**
         * Checks if given value is within range of this ranged missing value
         * @param value Double value to be checked
         * @return boolean telling if given value is within this range
         */
        boolean withinRange(Double value);

        /**
         * Checks if given value is within range of this ranged missing value
         * @param value Integer value to be checked. Will be cast to Double for checks
         * @return boolean telling if given value is within this range
         */
        boolean withinRange(Integer value);
    }

    static class PORMissingOpenLowRange extends PORMissingSingleValueRange implements PORMissingRangeValue {
        PORMissingOpenLowRange(String value) {
            super(PORMissingType.OPEN_LO, value);
        }

        @Override
        public boolean isValid() {
            return (!getValue().equals(Double.NaN));
        }

        @Override
        public boolean withinRange(PORValue value) throws NumberFormatException {
            if(value.type == PORValue.TYPE_NUMERIC) {
                return withinRange(value.value);
            } else {
                return false;
            }
        }

        @Override
        public boolean withinRange(Double value) {
            return getValue().compareTo(value) >= 0;
        }

        @Override
        public boolean withinRange(String value) throws NumberFormatException {
            Double d = asDouble(value);
            return withinRange(d);
        }

        @Override
        public boolean withinRange(Integer value) {
            return withinRange(new Double(value));
        }
    }

    static class PORMissingOpenHighRange extends PORMissingSingleValueRange implements PORMissingRangeValue {
        PORMissingOpenHighRange(String value) {
            super(PORMissingType.OPEN_HI, value);
        }

        @Override
        public boolean isValid() {
            return (!getValue().equals(Double.NaN));
        }

        @Override
        public boolean withinRange(PORValue value) throws NumberFormatException {
            if(value.type == PORValue.TYPE_NUMERIC) {
                return withinRange(value.value);
            } else {
                return false;
            }
        }

        @Override
        public boolean withinRange(Double value) {
            return getValue().compareTo(value) <= 0;
        }

        @Override
        public boolean withinRange(String value) throws NumberFormatException {
            Double d = asDouble(value);
            return withinRange(d);
        }

        @Override
        public boolean withinRange(Integer value) {
            return withinRange(new Double(value));
        }
    }

    static class PORMissingClosedRange extends PORMissingValueHolder implements PORMissingRangeValue {
        private final Double loValue;
        private final Double hiValue;

        PORMissingClosedRange(String loValue, String hiValue) {
            super(PORMissingType.RANGE_CLOSED, true, true);
            Double dLo = asDouble(loValue);
            Double dHi = asDouble(hiValue);

            // Set low and high values taking into consideration the distinct cases of 0.0d and -0.0d.
            // This enables clear range comparisons
            if(dLo.equals(0.0d)) {
                this.loValue = -0.0d;
            } else {
                this.loValue = dLo;
            }

            if(dHi.equals(-0.0d)) {
                this.hiValue = 0.0d;
            } else {
                this.hiValue = dHi;
            }
        }

        Double getLoValue() {
            return loValue;
        }

        Double getHiValue() {
            return hiValue;
        }

        boolean isLoInteger() {
            return super.isInteger(loValue);
        }

        boolean isHiInteger() {
            return super.isInteger(hiValue);
        }

        Integer loAsInteger() {
            return super.asInteger(loValue);
        }

        Integer hiAsInteger() {
            return super.asInteger(hiValue);
        }

        @Override
        public boolean isValid() {
            if(loValue.equals(Double.NaN) || hiValue.equals(Double.NaN)) {
                return false;
            }
            return true;
        }

        @Override
        public boolean withinRange(PORValue value) throws NumberFormatException {
            if(value.type == PORValue.TYPE_NUMERIC) {
                return withinRange(value.value);
            } else {
                return false;
            }
        }

        @Override
        public boolean withinRange(Double value) {
            return (loValue.compareTo(value) >= 0 && hiValue.compareTo(value) <= 0);
        }

        @Override
        public boolean withinRange(String value) throws NumberFormatException {
            Double d = asDouble(value);
            return withinRange(d);
        }

        @Override
        public boolean withinRange(Integer value) {
            return withinRange(new Double(value));
        }
    }

    static class PORVariableValueLabel {

        private final String label;
        private final PORValue porValue;

        private final String value;
        private final boolean missing;


        PORVariableValueLabel(String label, PORValue porValue, boolean missing) {
            this.label = label;
            this.porValue = porValue;
            this.missing = missing;

            if(isNumeric()) {
                double dVal = asDouble();
                int iVal = (int) dVal;
                if(dVal == (double)iVal) {
                    value = iVal+"";
                } else {
                    value = dVal+"";
                }
            } else {
                value = porValue.value;
            }
        }

        String getLabel() {
            return label;
        }

        PORValue asPORValue() {
            return porValue;
        }

        String getValue() {
            return value;
        }

        boolean isMissing() {
            return missing;
        }

        boolean isNumeric() {
            if(porValue.type == PORValue.TYPE_NUMERIC) {
                return true;
            } else {
                return false;
            }
        }

        double asDouble() throws NumberFormatException {
            if(isNumeric()) {
                return PORUtil.parseDouble(porValue.value);
            }
            throw new NumberFormatException();
        }
    }

    static abstract class PORVariableData {
        private final PORVariableType type;
        private final boolean numerical;

        public PORVariableData(PORVariableType type, boolean numerical) {
            this.type = type;
            this.numerical = numerical;
        }

        PORVariableType getType() {
            return type;
        }

        boolean isNumerical() {
            return numerical;
        }

        @Override
        public String toString() {
            return type.name();
        }

        static enum PORVariableType {
            SYSMISS,
            //INTEGER,
            //DOUBLE,
            NUMERIC,
            STRING
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

    static class PORVariableDataMissing extends PORVariableData {
        public PORVariableDataMissing() {
            super(PORVariableType.SYSMISS, false);
        }

        @Override
        public String toString() {
            return "SYSMISS";
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;

            return true;
        }
    }

    /*static class PORVariableDataInt extends PORVariableData {
        private final int value;

        public PORVariableDataInt(int value) {
            super(PORVariableType.INTEGER, true);
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return ""+value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PORVariableDataInt that = (PORVariableDataInt) o;

            if (value != that.value) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + value;
            return result;
        }
    }*/

    /*static class PORVariableDataDouble extends PORVariableData {
        private final double value;

        public PORVariableDataDouble(double value) {
            super(PORVariableType.DOUBLE, true);
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PORVariableDataDouble that = (PORVariableDataDouble) o;

            if (Double.compare(that.value, value) != 0) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            long temp;
            temp = Double.doubleToLongBits(value);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            // TODO: needs formatting
            return ""+value;
        }
    }*/

    static class PORVariableDataNumeric extends PORVariableData {
        private final double value;
        private final boolean isInteger;

        public PORVariableDataNumeric(double value) {
            super(PORVariableType.NUMERIC, true);
            this.value = value;

            int i = (int)value;
            isInteger = (value == (double)i);
        }

        public double getValue() {
            return value;
        }

        public boolean isInteger() {
            return this.isInteger;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PORVariableDataNumeric that = (PORVariableDataNumeric) o;

            if (Double.compare(that.value, value) != 0) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            long temp;
            temp = Double.doubleToLongBits(value);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            // TODO: needs formatting
            if(isInteger()) {
                return (int)value+"";
            } else {
                return ""+value;
            }
        }
    }

    static class PORVariableDataString extends PORVariableData {
        private final String value;

        public PORVariableDataString(String value) {
            super(PORVariableType.STRING, false);
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PORVariableDataString that = (PORVariableDataString) o;

            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    static class PORAnswerMapper implements PORMatrixVisitor {
        private final List<PORVariableHolder> holders;

        public PORAnswerMapper(List<PORVariableHolder> holders) {
            this.holders = holders;
        }

        // INTERFACE IMPLEMENTATION
        //==========================

        @Override
        public void matrixBegin(int xdim, int ydim, int[] xtypes) {

        }

        @Override
        public void matrixEnd() {
        }

        @Override
        public void rowBegin(int y) {

        }

        @Override
        public void rowEnd(int y) {

        }

        @Override
        public void columnSysmiss(int x, byte[] data, int len) {
            holders.get(x).addMissing();
        }

        @Override
        public void columnNumeric(int x, byte[] data, int len, double value) {

            // Determine whether an integer or decimal
            /*int ivalue = (int) value;
            if (value == (double) ivalue) {
                // Integer
                holders.get(x).addInt(ivalue);
            } else {
                // Decimal
                holders.get(x).addDouble(value);
            }*/
            holders.get(x).addNumeric(value);
        }

        @Override
        public void columnString(int x, byte[] data, int base, int len) {
            String valstr = new String(data, base, len-base);
            holders.get(x).addString(valstr);
        }

    }

    static class PORVariableDataComparator implements Comparator<PORVariableData> {
        /**
         * Compares two PORVariableData objects and sorts them depending on their type and value
         * Numbers are always considered smaller than strings and sysmiss values are always larger than strings.
         *
         * @param o1
         * @param o2
         * @return -1 if o1 is smaller than o2, 0 if equal, 1 if o1 is larger than o2
         */
        @Override
        public int compare(PORVariableData o1, PORVariableData o2) {
            // Two sysmiss are always equal
            if(o1.type == PORVariableData.PORVariableType.SYSMISS && o1.type == PORVariableData.PORVariableType.SYSMISS) {
                return 0;
            }
            // SYSMISS is always larger than non sysmiss
            if(o1.type == PORVariableData.PORVariableType.SYSMISS && o2.type != PORVariableData.PORVariableType.SYSMISS) {
                return 1;
            }
            if(o2.type == PORVariableData.PORVariableType.SYSMISS && o1.type != PORVariableData.PORVariableType.SYSMISS) {
                return -1;
            }

            // String is always larger than
            if(o1.type == PORVariableData.PORVariableType.STRING && o2.type == PORVariableData.PORVariableType.NUMERIC) {
                return 1;
            }
            if(o2.type == PORVariableData.PORVariableType.STRING && o1.type == PORVariableData.PORVariableType.NUMERIC) {
                return -1;
            }

            // Both variables have the same type, use correct comparison
            if(o1.type == o2.type && o1.type == PORVariableData.PORVariableType.NUMERIC) {
                // Both are numeric
                PORVariableDataNumeric n1 = (PORVariableDataNumeric)o1;
                PORVariableDataNumeric n2 = (PORVariableDataNumeric)o2;
                if(n1.getValue() < n2.getValue()) {
                    return -1;
                }
                if(n1.getValue() > n2.getValue()) {
                    return 1;
                }
                return 0;
            }
            if(o1.type == o2.type && o1.type == PORVariableData.PORVariableType.STRING) {
                // Both are strings
                return o1.toString().compareTo(o2.toString());
            }
            // Don't know what is going on, treat as equal
            return 0;
        }
    }

    static class PORNumericVariableDataComparator implements Comparator<PORVariableData> {
        /**
         * Compares two PORVariableData objects through numerical comparison.
         * Non numerical data is always smaller than numerical data but order between non numerical data is not guaranteed.
         * Null values are always smaller than non null values
         * @param o1
         * @param o2
         * @return -1 if o1 is smaller than o2, 0 if equal, 1 if o1 is larger than o2
         */
        @Override
        public int compare(PORVariableData o1, PORVariableData o2) {
            if(!o1.isNumerical() && !o2.isNumerical())
                return 0;
            if(!o1.isNumerical())
                return -1;
            if(!o2.isNumerical())
                return 1;

            /*Double d1 = null;
            Double d2 = null;
            if(o1.type == PORVariableData.PORVariableType.INTEGER)
                d1 = new Double(((PORVariableDataInt) o1).getValue());
            else if(o1.type == PORVariableData.PORVariableType.DOUBLE)
                d1 = ((PORVariableDataDouble) o1).getValue();

            if(o2.type == PORVariableData.PORVariableType.INTEGER)
                d2 = new Double(((PORVariableDataInt) o2).getValue());
            else if(o2.type == PORVariableData.PORVariableType.DOUBLE)
                d2 = ((PORVariableDataDouble) o2).getValue();*/

            Double d1 = ((PORVariableDataNumeric)o1).getValue();
            Double d2 = ((PORVariableDataNumeric)o2).getValue();

            if(d1 == null && d2 == null)
                return 0;
            if(d1 == null)
                return -1;
            if(d2 == null)
                return 1;

            return d1.compareTo(d2);
        }
    }
}