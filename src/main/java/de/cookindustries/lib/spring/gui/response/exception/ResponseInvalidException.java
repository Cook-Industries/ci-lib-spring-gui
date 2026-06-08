package de.cookindustries.lib.spring.gui.response.exception;

/**
 * @since 3.6.1
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public class ResponseInvalidException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public ResponseInvalidException(String msg)
    {
        super(msg);
    }

    public ResponseInvalidException(Throwable cause)
    {
        super(cause);
    }

    public ResponseInvalidException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

}
