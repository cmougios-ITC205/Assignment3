/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.time.LocalDate;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.Date;
import library.borrowbook.BorrowBookControl;
import library.borrowbook.BorrowBookUI;
import library.entities.Book;
import library.entities.IBook;
import library.entities.ILibrary;
import library.entities.ILoan;
import library.entities.IPatron;
import library.entities.Library;
import library.entities.Loan;
import library.entities.Patron;
import library.entities.helpers.BookHelper;
import library.entities.helpers.LoanHelper;
import library.entities.helpers.PatronHelper;
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
public class TestBRLSIntegration {

       @Mock BookHelper bookhelper;
       @Mock  PatronHelper patronhelper;
       @Mock LoanHelper loanhelper;
   
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
	public void patronCanBorrowTest() {
           
	    Patron patron = new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1);
            Library library = new Library(bookhelper,patronhelper,loanhelper);
            assertTrue(library.patronCanBorrow(patron));
	}
        @Test
	public void patronCantBorrow_FineLimit() {
		
            Library library = new Library(bookhelper,patronhelper,loanhelper);
            Patron patron = new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1);
	    patron.incurFine(1);
            assertFalse(library.patronCanBorrow(patron));
	}
        @Test
	public void patronCantBorrow_OverdueLoan() {
		
            Library library = new Library(bookhelper,patronhelper,loanhelper);
	    Patron patron = new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1);
	    Loan loan1 = new Loan(new Book("bookauthor","booktitle","bookId1",1),patron);
	    loan1.commit(1,java.sql.Date.valueOf(LocalDate.now().minusMonths(2)));
	    loan1.checkOverDue(java.sql.Date.valueOf(LocalDate.now()));
	    assertFalse(library.patronCanBorrow(patron));
	}
        public void issueLoanTest() {
	
            Library library = new Library(bookhelper,patronhelper,loanhelper);
            ILoan iloan = library.issueLoan(new Book("bookauthor","booktitle","bookId1",1),  new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1));
	    assertTrue(iloan instanceof ILoan);
	}
        @Test
	public void commitLoanTest() {
		
            Library library = new Library(bookhelper,patronhelper,loanhelper);
            Patron patron = new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1);
	    Loan loan1 = new Loan(new Book("bookauthor","booktitle","bookId1",1),patron);
	    library.commitLoan(loan1);
	    assertTrue(library.getCurrentLoansList().contains(loan1));
	}
        @Test(expected = RuntimeException.class)
	public void commitLoanWithExceptionTest() {
	
            Loan loan = new Loan(new Book("bookauthor","booktitle","bookId1",1),  new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1));
	    loan.commit(1,java.sql.Date.valueOf(LocalDate.now().plusMonths(2)));
	    loan.commit(1,java.sql.Date.valueOf(LocalDate.now().plusMonths(2)));
           
	}
        @Test
	public void loanIsNotOverdueTest() {
            
            Loan loan = new Loan(new Book("bookauthor","booktitle","bookId1",1),  new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1));
            loan.commit(1,java.sql.Date.valueOf(LocalDate.now().plusMonths(2)));
	    assertFalse(loan.checkOverDue(java.sql.Date.valueOf(LocalDate.now())));
	}
        @Test
	public void loanIsOverdueTest() {
	
            Loan loan = new Loan(new Book("bookauthor","booktitle","bookId1",1),  new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1));
	    loan.commit(1,java.sql.Date.valueOf(LocalDate.now().minusMonths(3)));
	    assertTrue(loan.checkOverDue(java.sql.Date.valueOf(LocalDate.now())));
	}
        @Test
	public void patronHasOverdueLoansTest() {
            
            Patron patron =new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1);
            Loan loan = new Loan(new Book("bookauthor","booktitle","bookId1",1),patron);
	    loan.commit(1,java.sql.Date.valueOf(LocalDate.now().minusMonths(3)));
	    loan.checkOverDue(java.sql.Date.valueOf(LocalDate.now()));
	    assertTrue(patron.hasOverDueLoans());
	}
        @Test
	public void patronHasNoOverdueLoansTest() {
            
	    Patron patron =new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1);
            Loan loan = new Loan(new Book("bookauthor","booktitle","bookId1",1),patron);
            loan.commit(1,java.sql.Date.valueOf(LocalDate.now().plusMonths(7)));
            loan.checkOverDue(java.sql.Date.valueOf(LocalDate.now()));
	    assertFalse(patron.hasOverDueLoans());
	}
        @Test(expected = RuntimeException.class)
	public void duplicateLoanTakeOutTest() {
		
            Patron patron =new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1);
            Loan loan1 = new Loan(new Book("bookauthor","booktitle","bookId1",1),patron);
            Loan loan2 = new Loan(new Book("bookauthor2","booktitle2","bookId2",2),patron);
            loan1.commit(1,java.sql.Date.valueOf(LocalDate.now()));
	    loan2.commit(1,java.sql.Date.valueOf(LocalDate.now().minusMonths(5)));
            patron.takeOutLoan(loan1);
            patron.takeOutLoan(loan2);
	}
        @Test
	public void bookIsAvailableTest() {
	
            Book book = new Book("bookauthor","booktitle","bookId1",1);
            assertTrue(book.isAvailable());
	}
        @Test
	public void bookIsNotAvailableTest() {
            
            Book book = new Book("bookauthor","booktitle","bookId11",11);
            Patron patron2 = new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1);
            Loan loan = new Loan(book,patron2);
            loan.commit(1,java.sql.Date.valueOf(LocalDate.now()));
	    assertFalse(book.isAvailable());
	}
        @Test
	public void borrowFromLibraryTest() {
            
            Book book = new Book("bookauthor","booktitle","bookId11",11);
            book.borrowFromLibrary();
	}
        @Test(expected = RuntimeException.class)
	public void borrowFromLibraryWithException() {
		
            Book book = new Book("bookauthor","booktitle","bookId111",111);
            Patron patron = new Patron("Mougios","Con","cmougios@tpg.com.au",1234,1);
            Loan loan = new Loan(book,patron);
	    loan.commit(3,java.sql.Date.valueOf(LocalDate.now()));
	    book.borrowFromLibrary();
	}
        @Test
	public void cardSwipeTest() {
		
            Library library = new Library(bookhelper,new PatronHelper(),loanhelper);
	    library.addPatron("Mougios", "Con", "cmougios@tpg.com.au", 1234);
	    IPatron patron = library.getPatronList().get(0);
	    BorrowBookControl bbc = new BorrowBookControl(library);
            new BorrowBookUI(bbc);
	    bbc.cardSwiped(patron.getId());
	}
        @Test(expected = RuntimeException.class)
	public void cardSwipeException() {
            
            Library library = new Library(bookhelper,patronhelper,loanhelper);
            new BorrowBookControl(library).cardSwiped(000);
	}
        @Test
	public void bookScannedTest() {
	
            Library library = new Library(new BookHelper(),new PatronHelper(),new LoanHelper());
            IBook book = library.addBook("bookauthor","booktitle","bookId111");
            IPatron patron = library.addPatron("Mougios","Con","cmougios@tpg.com.au",1234);
            BorrowBookControl bbc = new BorrowBookControl(library);
            new BorrowBookUI(bbc);
            bbc.cardSwiped(patron.getId());
            bbc.bookScanned(book.getId());
	}
        @Test(expected = RuntimeException.class)
	public void bookScanWithException() {
		
            Library library = new Library(bookhelper,patronhelper,loanhelper);
            IBook book = library.addBook("bookauthor","booktitle","bookId111");
            BorrowBookControl bbc = new BorrowBookControl(library);
            bbc.bookScanned(book.getId());
	}
        @Test
	public void borrowBookAfterPayFineTest() {
	
            Library library = new Library(new BookHelper(),new PatronHelper(),new LoanHelper());
            IPatron patron = library.addPatron("Mougios","Con","cmougios@tpg.com.au",1234);
            IBook book = library.addBook("bookauthor","booktitle","bookId111");
            patron.incurFine(5);
            library.payFine(patron,6);
            ILoan loan1 = library.issueLoan(book, patron);
            library.commitLoan(loan1);
            assertTrue(library.patronCanBorrow(patron));
	}
     
       
   
       
       
       
}

