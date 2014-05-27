package fi.uta.fsd.metka.data.variableParsing.impl;

import spssio.por.PORMatrixVisitor;
import spssio.por.PORValue;
import spssio.por.PORValueLabels;
import spssio.por.PORVariable;
import spssio.util.NumberParser;
import spssio.util.NumberSystem;

import java.util.*;

class PORUtil {
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
        private final List<PORVariableValueLabel> labels;
        private final List<PORVariableData> data;

        public PORVariableHolder(PORVariable var) {
            this.var = var;
            labels = new ArrayList<>();
            data = new ArrayList<>();
        }

        public PORVariable asVariable() {
            return var;
        }

        public List<PORVariableValueLabel> getLabels() {
            List<PORVariableValueLabel> copy = new ArrayList<>(labels);
            return copy;
        }

        public List<PORVariableData> getData() {
            List<PORVariableData> copy = new ArrayList<>(data);
            return copy;
        }

        public List<PORVariableData> getNumericalDataWithoutMissing() {
            List<PORVariableData> copy = new ArrayList<>();
            for(PORVariableData d : data) {
                if(d.isNumerical()) {
                    copy.add(d);
                }
            }
            return copy;
        }

        public void addLabels(PORValueLabels labels) {
            for(PORValue key : labels.mappings.keySet()) {
                PORVariableValueLabel valLbl = new PORVariableValueLabel(labels.mappings.get(key), key);
                this.labels.add(valLbl);
            }
        }

        public boolean hasLabels() {return labels.size() > 0;}

        public void addMissing() {
            data.add(new PORVariableDataMissing());
        }

        public void addInt(int i) {
            data.add(new PORVariableDataInt(i));
        }

        public void addDouble(double d) {
            data.add(new PORVariableDataDouble(d));
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

    static class PORVariableValueLabel {
        private static NumberParser parser = new NumberParser(new NumberSystem(10, null));
        private final String label;
        private final PORValue porValue;

        public final String value;

        PORVariableValueLabel(String label, PORValue porValue) {
            this.label = label;
            this.porValue = porValue;

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

        boolean isNumeric() {
            if(porValue.type == PORValue.TYPE_NUMERIC) {
                return true;
            } else {
                return false;
            }
        }

        double asDouble() throws NumberFormatException {
            if(isNumeric()) {
                return parser.parseDouble(porValue.value);
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
            INTEGER,
            DOUBLE,
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

    static class PORVariableDataInt extends PORVariableData {
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
    }

    static class PORVariableDataDouble extends PORVariableData {
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
            int ivalue = (int) value;
            if (value == (double) ivalue) {
                // Integer
                holders.get(x).addInt(ivalue);
            } else {
                // Decimal
                holders.get(x).addDouble(value);
            }
        }

        @Override
        public void columnString(int x, byte[] data, int base, int len) {
            String valstr = new String(data, base, len-base);
            holders.get(x).addString(valstr);
        }

    }

    static class PORVariableDataComparator implements Comparator<PORVariableData> {
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

            Double d1 = null;
            Double d2 = null;
            if(o1.type == PORVariableData.PORVariableType.INTEGER)
                d1 = new Double(((PORVariableDataInt) o1).getValue());
            else if(o1.type == PORVariableData.PORVariableType.DOUBLE)
                d1 = ((PORVariableDataDouble) o1).getValue();

            if(o2.type == PORVariableData.PORVariableType.INTEGER)
                d2 = new Double(((PORVariableDataInt) o2).getValue());
            else if(o2.type == PORVariableData.PORVariableType.DOUBLE)
                d2 = ((PORVariableDataDouble) o2).getValue();

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