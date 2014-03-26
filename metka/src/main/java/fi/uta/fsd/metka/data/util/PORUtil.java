package fi.uta.fsd.metka.data.util;

import org.apache.commons.lang3.math.NumberUtils;
import spssio.por.PORMatrixVisitor;
import spssio.por.PORValueLabels;
import spssio.por.PORVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
        private final List<PORValueLabels> labels;
        private final List<PORVariableData> data;
        private int missing;

        public PORVariableHolder(PORVariable var) {
            this.var = var;
            labels = new ArrayList<PORValueLabels>();
            data = new ArrayList<PORVariableData>();
            missing = 0;
        }

        public PORVariable asVariable() {
            return var;
        }

        public List<PORValueLabels> getLabels() {
            List<PORValueLabels> copy = new ArrayList<PORValueLabels>(labels);
            return copy;
        }

        public List<PORVariableData> getData() {
            List<PORVariableData> copy = new ArrayList<PORVariableData>(data);
            return copy;
        }

        public List<PORVariableData> getNumericalDataWithoutMissing() {
            List<PORVariableData> copy = new ArrayList<PORVariableData>();
            for(PORVariableData d : data) {
                if(!d.missing && !(d instanceof PORVariableDataString)) {
                    copy.add(d);
                } else if(d instanceof PORVariableDataString) {
                    if(NumberUtils.isNumber(((PORVariableDataString) d).getValue())) {
                        copy.add(d);
                    }
                }
            }
            return copy;
        }

        public void addLabels(PORValueLabels labels) {
            this.labels.add(labels);
        }

        public boolean hasLabels() {return labels.size() > 0;}

        public int getMissingNro() {return missing;}

        public void addMissing() {
            data.add(new PORVariableData(true));
            missing++;
        }

        public void addInt(int i) {
            data.add(new PORVariableDataInt(false, i));
        }

        public void addDouble(double d) {
            data.add(new PORVariableDataDouble(false, d));
        }

        public void addString(String s) {
            data.add(new PORVariableDataString(false, s));
        }

        @Override
        public int hashCode() {
            return var.name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof PORVariableHolder) {
                PORVariableHolder other = (PORVariableHolder)obj;

                return var.name.equals(other.var.name);
            }/* else if(obj instanceof PORVariable) {
                PORVariable other = (PORVariable) obj;

                return var.name.equals(other.name);
            }*/ else {
                return false;
            }
        }
    }

    static class PORVariableData {
        private boolean missing = false;

        public PORVariableData(boolean missing) {
            this.missing = missing;
        }

        public boolean isMissing() {
            return missing;
        }

        public void setMissing(boolean missing) {
            this.missing = missing;
        }

        @Override
        public String toString() {
            return (missing)?"SYSMISS":" ";
        }
    }

    static class PORVariableDataInt extends PORVariableData {
        private final int value;

        public PORVariableDataInt(boolean missing, int value) {
            super(missing);
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return ""+value;
        }
    }

    static class PORVariableDataDouble extends PORVariableData {
        private final double value;

        public PORVariableDataDouble(boolean missing, double value) {
            super(missing);
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        @Override
        public String toString() {
            // TODO: needs formatting
            return ""+value;
        }
    }

    static class PORVariableDataString extends PORVariableData {
        private final String value;

        public PORVariableDataString(boolean missing, String value) {
            super(missing);
            this.value = value;
        }

        public String getValue() {
            return value;
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

            String valstr = null;
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
}