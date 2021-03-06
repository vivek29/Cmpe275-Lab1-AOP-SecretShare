package com.vivek.cmpe275.lab1.aop;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.vivek.Exception.UnauthorizedException;
import com.vivek.Model.Secret;
import com.vivek.Service.DBService;
import com.vivek.Service.SecretService;


/**
 * Unit test for simple App.
 */
public class AppTest {
	
	@Autowired
	DBService dBService;
	
	ApplicationContext ctx;
	SecretService secretService;
	
	/**
	 * Initialize context
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		
		ctx = new ClassPathXmlApplicationContext("beans.xml");
		secretService = (SecretService) ctx.getBean("secretServiceImpl");
	}
	
	
    /** TestA
    Bob cannot read Alice’s secret, which has not been shared with Bob. 
    "Unauthorized Exception"
     */
    @Test(expected=UnauthorizedException.class)
	public void testA(){
		System.out.println("testA");
		UUID secretId = secretService.storeSecret("Alice", new Secret());
		secretService.readSecret("Bob", secretId);
	}
    
    /** TestB
     * Alice shares a secret with Bob, and Bob can read it.
     */
    @Test
    public void testB(){
		System.out.println("testB");
		UUID secretId = secretService.storeSecret("Alice", new Secret());
		secretService.shareSecret("Alice", secretId, "Bob");
		secretService.readSecret("Bob", secretId);
	}
	
    /** Test C
     * Alice shares a secret with Bob, and Bob shares Alice’s it with Carl, and Carl can read this secret.
     */
    @Test
	public void testC(){
		System.out.println("testC");
		UUID secretId = secretService.storeSecret("Alice", new Secret());
		secretService.shareSecret("Alice", secretId, "Bob");
		secretService.shareSecret("Bob", secretId, "Carl");
		secretService.readSecret("Carl", secretId);
	}
    
    /** Test D
     * Alice shares her secret with Bob; Bob shares Carl’s secret with Alice and encounters UnauthorizedException
     */
    @Test(expected=UnauthorizedException.class)
	public void testD(){
		System.out.println("testD");
		UUID secretId_Alice = secretService.storeSecret("Alice", new Secret());
		UUID secretId_Carl = secretService.storeSecret("Carl", new Secret());
		secretService.shareSecret("Alice", secretId_Alice, "Bob");
		secretService.shareSecret("Bob", secretId_Carl, "Alice");
	}
    
    /** Test E
     * Alice shares a secret with Bob, Bob shares it with Carl, Alice unshares it with Carl,
     * and Carl cannot read this secret anymore.
     */
    @Test(expected=UnauthorizedException.class)
	public void testE(){
		System.out.println("testE");
		UUID secretId = secretService.storeSecret("Alice", new Secret());
		secretService.shareSecret("Alice", secretId, "Bob");
		secretService.shareSecret("Bob", secretId, "Carl");
		secretService.unshareSecret("Alice", secretId, "Carl");
		secretService.readSecret("Carl", secretId);
	}
    
    /** Test F
     * Alice shares a secret with Bob and Carl; Carl shares it with Bob, then Alice unshares it with Bob;
     * Bob cannot read this secret anymore.
     */
    @Test(expected=UnauthorizedException.class)
	public void testF(){
		System.out.println("testF");
		UUID secretId = secretService.storeSecret("Alice", new Secret());
		secretService.shareSecret("Alice", secretId, "Bob");
		secretService.shareSecret("Alice", secretId, "Carl");
		secretService.shareSecret("Carl", secretId, "Bob");
		secretService.unshareSecret("Alice", secretId, "Bob");
		secretService.readSecret("Bob", secretId);
	}
    
    /** Test G
     * Alice shares a secret with Bob; Bob shares it with Carl, and then unshares it with Carl.
     * Carl can still read this secret.
     */
    @Test
	public void testG(){
		System.out.println("testG");
		UUID secretId = secretService.storeSecret("Alice", new Secret());
		secretService.shareSecret("Alice", secretId, "Bob");
		secretService.shareSecret("Bob", secretId, "Carl");
		secretService.unshareSecret("Bob", secretId, "Carl");
		secretService.readSecret("Carl", secretId);
	}
    
    /** Test H
     * Alice shares a secret with Bob; Carl unshares it with Bob, and encounters UnauthorizedException
     */
    @Test(expected=UnauthorizedException.class)
	public void testH(){
		System.out.println("testH");
		UUID secretId = secretService.storeSecret("Alice", new Secret());
		secretService.shareSecret("Alice", secretId, "Bob");
		secretService.unshareSecret("Carl", secretId, "Bob");
	}
    
    /** Test I
     * Alice shares a secret with Bob; Bob shares it with Carl; Alice unshares it with Bob;
     * Bob shares it with Carl with again, and encounters UnauthorizedException
     */
    @Test(expected=UnauthorizedException.class)
	public void testI(){
		System.out.println("testI");
		UUID secretId = secretService.storeSecret("Alice", new Secret());
		secretService.shareSecret("Alice", secretId, "Bob");
		secretService.shareSecret("Bob", secretId, "Carl");
		secretService.unshareSecret("Alice", secretId, "Bob");
		secretService.shareSecret("Bob", secretId, "Carl");
	}
    
    /** Test J
     * Alice stores the same secrete object twice, and get two different UUIDs
     */
    @Test
	public void testJ(){
		System.out.println("testJ");
		UUID secretId1 = secretService.storeSecret("Alice", new Secret());
		UUID secretId2 = secretService.storeSecret("Alice", new Secret());
		boolean isSame = (secretId1==secretId2);
		Assert.assertEquals(false, isSame);
	}
}