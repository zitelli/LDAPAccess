package br.com.alura.LDAPAccess.config;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;
import java.io.*;

public class EncryptionUtils
{
  public static String encrypt( String source, String chave )
  {
	try
	{
	  // Get our secret key
	  Key key = getKey(chave);

	  // Create the cipher
	  Cipher desCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");

	  // Initialize the cipher for encryption
	  desCipher.init(Cipher.ENCRYPT_MODE, key);

	  // Our cleartext as bytes
	  byte[] cleartext = source.getBytes();

	  // Encrypt the cleartext
	  byte[] ciphertext = desCipher.doFinal(cleartext);

	  // Return a String representation of the cipher text
	  return getString( ciphertext );

	}
	catch( Exception e )
	{
	  e.printStackTrace();
	}
	return null;
  }

  public static String generateKey()
  {
	try
	{
	  KeyGenerator keygen = KeyGenerator.getInstance("DESede");
	  SecretKey desKey = keygen.generateKey();
	  byte[] bytes = desKey.getEncoded();
	  return getString( bytes );
	}
	catch( Exception e )
	{
	  e.printStackTrace();
	  return null;
	}
  }

  public static String decrypt( String source, String chave )
  {
	try
	{
	  // Get our secret key
	  Key key = getKey(chave);

	  // Create the cipher
	  Cipher desCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");

	  // Encrypt the cleartext
	  byte[] ciphertext = getBytesDecript( source );

	  // Initialize the same cipher for decryption
	  desCipher.init(Cipher.DECRYPT_MODE, key);

	  // Decrypt the ciphertext
	  byte[] cleartext = desCipher.doFinal(ciphertext);

	  // Return the clear text
	  return new String( cleartext );
	}
	catch( Exception e )
	{
	  e.printStackTrace();
	}
	return null;
  }

  private static Key getKey(String chave)
  {
	try
	{
	  byte[] bytes = chave.getBytes();
	  DESedeKeySpec pass = new DESedeKeySpec( bytes );
	  SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
	  SecretKey s = skf.generateSecret(pass);
	  return s;
	}
	catch( Exception e )
	{
	  e.printStackTrace();
	}
	return null;
  }

  /**
   * Returns true if the specified text is encrypted, false otherwise
   */
  public static boolean isEncrypted( String text )
  {
	// If the string does not have any separators then it is not
	// encrypted
	if( text.indexOf( '-' ) == -1 )
	{
	  ///System.out.println( "text is not encrypted: no dashes" );
	  return false;
	}

	StringTokenizer st = new StringTokenizer( text, "-", false );
	while( st.hasMoreTokens() )
	{
	  String token = st.nextToken();
	  if( token.length() > 3 )
	  {
		//System.out.println( "text is not encrypted: length of token greater than 3: " + token );
		return false;
	  }
	  for( int i=0; i<token.length(); i++ )
	  {
		if( !Character.isDigit( token.charAt( i ) ) )
		{
		  //System.out.println( "text is not encrypted: token is not a digit" );
		  return false;
		}
	  }
	}
	//System.out.println( "text is encrypted" );
	return true;
  }

  private static String getString( byte[] bytes )
  {
	StringBuffer sb = new StringBuffer();
	for( int i=0; i<bytes.length; i++ )
	{
	  byte b = bytes[ i ];
	  sb.append( ( int )( 0x00FF & b ) );
	  if( i+1 <bytes.length )
	  {
		sb.append( "-" );
	  }
	}
	return sb.toString();
  }

  private static byte[] getBytesDecript( String str )
  {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	StringTokenizer st = new StringTokenizer( str, "-", false );
	while( st.hasMoreTokens() )
	{
	  int i = Integer.parseInt( st.nextToken() );
	  bos.write( ( byte )i );
	}
	return bos.toByteArray();
  }

  private static byte[] getBytes( String str )
	{
	  return str.getBytes();
	}

  public static void main( String[] args )
  {

	String chave = "WAallfkrIeDdSokSyaWnatlokser2005";

	System.out.println( "--------------Gera a chave--------------");
	System.out.println( "Chave: " + chave);
	System.out.println( "----------------------------------------");
	String s1 = "1qaz2wsx";
	System.out.println( "Senha a ser criptografada: "+s1 );

	String senhacriptografada = EncryptionUtils.encrypt( s1, chave );
	//System.out.println( "Senha criptografada = " + senhacriptografada );
	System.out.println( "Senha criptografada = " + senhacriptografada );
	System.out.println( "----------------------------------------");
	System.out.println( senhacriptografada);
	System.out.println( "Senha descriptografada = " + EncryptionUtils.decrypt( "237-218-162-92-174-231-213-83-85-121-130-114-59-71-174-149", chave ) );
	System.out.println( "----------------------------------------");
  }



  public static void showProviders()
  {
	try
	{
	  Provider[] providers = Security.getProviders();
	  for( int i=0; i<providers.length; i++ )
	  {
		System.out.println( "Provider: " +
providers[ i ].getName() + ", " + providers[ i ].getInfo() );
		for( Iterator itr = providers[ i ].keySet().iterator();
itr.hasNext(); )
		{
		  String key = ( String )itr.next();
		  String value = ( String )providers[ i ].get( key );
		  System.out.println( "\t" + key + " = " + value );
		}

	  }
	}
	catch( Exception e )
	{
	  e.printStackTrace();
	}
  }
}
