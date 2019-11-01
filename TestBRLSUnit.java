/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.time.LocalDate;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.Date;
import library.entities.Book;
import library.entities.IBook;
import library.entities.ILibrary;
import library.entities.ILoan;
import library.entities.IPatron;
import library.entities.Loan;
import library.entities.Patron;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;


/*test the isAvailable method of Book ojbect with Mockito annotation*/
@RunWith(MockitoJUnitRunner.class)
public class TestBRLSUnit {

      
       @Mock IBook book;
       @Mock IPatron patron;
       @Mock ILoan loan;
       @Mock ILibrary library;
     
   
       @Rule
       public ExpectedException thrown = ExpectedException.none();

      
       
       @Before
       public void setUp() throws Exception {
           
              initMocks(this); //initialise this mock object
              
       }

       @After
       public void tearDown() throws Exception {
       }


       @Test
	public void testCommitLoanNotPending() {
		
		Loan loan = new Loan(book,patron);
		doNothing().when(patron).takeOutLoan(loan);
		doNothing().when(book).borrowFromLibrary();
		loan.commit(1, java.sql.Date.valueOf(LocalDate.now().plusMonths(2)));
		verify(patron,times(1)).takeOutLoan(loan);
		verify(book,times(1)).borrowFromLibrary();
		
	}
        @Test(expected = RuntimeException.class)
	public void testCommitLoanPending() {
		
		Loan loan = new Loan(book,patron);
		doNothing().when(patron).takeOutLoan(loan);
		doNothing().when(book).borrowFromLibrary();
		loan.commit(1, java.sql.Date.valueOf(LocalDate.now().plusMonths(2)));
		loan.commit(1, java.sql.Date.valueOf(LocalDate.now().plusMonths(2)));
	}
        @Test
	public void testOverdueDateTrue() {
		
		Loan loan = new Loan(book,patron);
		doNothing().when(patron).takeOutLoan(loan);
		doNothing().when(book).borrowFromLibrary();
		loan.commit(1, java.sql.Date.valueOf(LocalDate.now().minusMonths(2)));
		assertTrue(loan.checkOverDue(java.sql.Date.valueOf(LocalDate.now())));
	}
        @Test
	public void testOverdueDateFalse() {
		
		Loan loan = new Loan(book,patron);
		doNothing().when(patron).takeOutLoan(loan);
		doNothing().when(book).borrowFromLibrary();
		loan.commit(1, java.sql.Date.valueOf(LocalDate.now().plusMonths(2)));
		assertFalse(loan.checkOverDue(java.sql.Date.valueOf(LocalDate.now())));
	}
        @Test
	public void patronHasNoOverdueLoans() {
		
		when(patron.hasOverDueLoans()).thenReturn(false);
		assertFalse(patron.hasOverDueLoans());
	}
        @Test
	public void patronHasOverdueLoans() {
		
		when(patron.hasOverDueLoans()).thenReturn(true);
		assertTrue(patron.hasOverDueLoans());
	}
        @Test
	public void testTakeOutLoan() {
		
		patron.takeOutLoan(loan);
	}
        @Test
	public void testBookIsAvailable() {
		
		when(book.isAvailable()).thenReturn(true);
		book.isAvailable();
	}
        @Test
	public void testBookIsNotAvailable() {
		
		when(book.isAvailable()).thenReturn(false);
		book.isAvailable();
	}
        @Test
	public void borrowFromLibraryTest() {
		
		book.borrowFromLibrary();
	}
        @Test
	public void patronCanBorrowTrueTest() {
		
		when(library.patronCanBorrow(patron)).thenReturn(true);
		assertTrue(library.patronCanBorrow(patron));
	}
        @Test
	public void patronCanBorrowFalseTest(){
		
		when(library.patronCanBorrow(patron)).thenReturn(false);
		assertFalse(library.patronCanBorrow(patron));
	}
        @Test
	public void libraryIssueLoanTest() {
		
		when(library.issueLoan(book,patron)).thenReturn((loan));
		ILoan loan = library.issueLoan(book, patron);
		assertTrue(loan instanceof ILoan);
	}
     
       
   
       
       
       
}

