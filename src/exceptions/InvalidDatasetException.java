package exceptions;

@SuppressWarnings("serial")
public class InvalidDatasetException extends Exception {
	public InvalidDatasetException(String msg){
		super(msg);
	}
}
