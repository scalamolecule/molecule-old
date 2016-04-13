package molecule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Functions
{
    public static long bind( long e )
    {
        return e;
    }

    public static Date date( String s ) throws ParseException
    {
        return ( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" ).parse( s ) );
    }
}
