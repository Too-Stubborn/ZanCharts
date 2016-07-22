package com.github.mikephil.charting.exception;

public class DrawingDataSetNotCreatedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DrawingDataSetNotCreatedException() {
		    super("Call ChartData's createNewDrawingDataSet() method");
	  }

}
