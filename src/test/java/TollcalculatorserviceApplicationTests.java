import com.tollapplication.model.Car;
import com.tollapplication.model.Foreign;
import com.tollapplication.model.Tractor;
import com.tollapplication.model.Vehicle;
import com.tollapplication.model.Motorbike;
import com.tollapplication.model.Diplomat;
import com.tollapplication.model.Emergency;
import com.tollapplication.model.Military;
import com.tollapplication.service.TollFeeCalculator;
import com.tollapplication.utils.constants.ErrorCodes;
import com.tollapplication.utils.constants.ServiceConstants;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@RunWith(MockitoJUnitRunner.class)
class TollcalculatorserviceApplicationTests {

	private final TollFeeCalculator tollFeeCalculator =  new TollFeeCalculator();

	private static LocalDate date;
	private static Vehicle car;
	private static int year;

	@Before
	void setUpObjects() {
		//tollFeeCalculator =  new TollFeeCalculator();
		List<LocalDateTime> list = new ArrayList<>();
	}

	@BeforeAll
	private static void setUp() {
		year = LocalDate.now(ZoneId.systemDefault()).getYear();
		date = LocalDate.of( year, 5, 18);
		car = new Car();
		/*Mockito.when();
		*/
	}

	@DisplayName("INVALID VEHICLE Test")
	@Test
	public void testInvalidVehicle() {
		RuntimeException re = assertThrows(RuntimeException.class, () ->
				tollFeeCalculator.getTollFee(null, (new LocalDateTime[] {
						LocalDateTime.of(date, LocalTime.of(6, 0))
				})));
		assertEquals(ErrorCodes.INVALID_VEHICLE, re.getMessage());
	}

	@DisplayName("INVALID DATES Test")
	@Test
	public void testInvalidDates() {
		RuntimeException re = assertThrows(RuntimeException.class, () -> tollFeeCalculator.getTollFee(car, (LocalDateTime[])null));
		assertEquals(ErrorCodes.DATES_EMPTY, re.getMessage());
		RuntimeException res = assertThrows(RuntimeException.class, () -> tollFeeCalculator.getTollFee(car, LocalDateTime.of(1993, 12, 27, 6, 0)));
		assertEquals(ErrorCodes.INVALID_DATE, res.getMessage());
		RuntimeException resp = assertThrows(RuntimeException.class, () -> tollFeeCalculator.getTollFee(car, LocalDateTime.of(0, 0, 0, 0, 0)));
		assertEquals(ErrorCodes.INVALID_DATE_ER, resp.getMessage());
		RuntimeException respo = assertThrows(RuntimeException.class, () -> tollFeeCalculator.getTollFee(car, LocalDateTime.of(0, 13, 0, 0, 0)));
		assertEquals(ErrorCodes.INVALID_DATE_MNTH, respo.getMessage());
	}

	@DisplayName("DIFFERENT DATES Test")
	@Test
	public void testDifferentDates() {
		RuntimeException re = assertThrows(RuntimeException.class,
				() -> tollFeeCalculator.getTollFee(car,(new LocalDateTime[] {
						LocalDateTime.of(year, 10, 26, 6, 0),
						LocalDateTime.of(year, 10, 27, 6, 0)}
				)));
		assertEquals(ErrorCodes.DIFFERENT_DATES, re.getMessage());
	}

	@DisplayName("SAME TIME test")
	@Test
	public void testSameTime() {
		int fee = tollFeeCalculator.getTollFee(car, (new LocalDateTime[] {
								LocalDateTime.of( year, 10, 26, 6, 0),
								LocalDateTime.of( year, 10, 26, 6, 0)}
								));
		assertEquals(8, fee);
	}

	@DisplayName("MULTIPLE FEE WITHIN AN HOUR, MAX FEE test")
	@Test
	public void testMultiFeeInHour() {
		int fee = tollFeeCalculator.getTollFee(car, new LocalDateTime[] {
						LocalDateTime.of( year, 10, 26, 14, 59),
						LocalDateTime.of( year, 10, 26, 15, 0),
						LocalDateTime.of( year, 10, 26, 15, 30)
		});
		assertEquals(18, fee);
	}

	@DisplayName("FREE VECHICLE Test")
	@Test
	public void testFreeVehicle() {
		LocalDateTime time = LocalDateTime.of(date, LocalTime.of(9, 1));
		assertEquals(0,  tollFeeCalculator.getTollFee(new Emergency(), time));
		assertEquals(0,  tollFeeCalculator.getTollFee(new Foreign(), time));
		assertEquals(0,  tollFeeCalculator.getTollFee(new Motorbike(), time));
		assertEquals(0,  tollFeeCalculator.getTollFee(new Military(), time));
		assertEquals(0,  tollFeeCalculator.getTollFee(new Tractor(), time));
		assertEquals(0,  tollFeeCalculator.getTollFee(new Diplomat(), time));

	}

	@DisplayName("WEEKEND FEE {SAT,SUN} Test")
	@Test
	public void testWeekendFee(){
		assertEquals(0,  tollFeeCalculator.getTollFee(car, LocalDateTime.of(year, 6, 19, 0, 0,0)));
		assertEquals(0,  tollFeeCalculator.getTollFee(car, LocalDateTime.of(year, 6, 19, 6, 0)));
		assertEquals(0,  tollFeeCalculator.getTollFee(car, LocalDateTime.of(year, 6, 20, 6, 0)));
		assertEquals(0,  tollFeeCalculator.getTollFee(car, LocalDateTime.of(year, 6, 20, 0, 0,0)));
		assertEquals(0,  tollFeeCalculator.getTollFee(car, LocalDateTime.of(year, 6, 20, 23, 59,59, 59)));

	}

	@DisplayName("HOLIDAY FEE Test")
	@Test
	public void testFreeDate() {
		assertEquals(0,  tollFeeCalculator.getTollFee(car, LocalDateTime.of(year, 12, 24, 0, 0, 0)));
		assertEquals(0,  tollFeeCalculator.getTollFee(car, LocalDateTime.of(year, 12, 24, 7, 0)));
		assertEquals(0,  tollFeeCalculator.getTollFee(car, LocalDateTime.of(year, 4, 13, 23, 59, 59)));
		assertEquals(0,  tollFeeCalculator.getTollFee(car, LocalDateTime.of(year, 3, 28, 7, 0)));
	}

	@DisplayName("DEFAULT FEE test")
	@Test
	public void testDefaultFee() {
		List<LocalDateTime> dates = new ArrayList<>();
		dates.add(LocalDateTime.of(date, LocalTime.of(0, 0))); // 0
		assertEquals(8, tollFeeCalculator.getTollFee(car, dates.toArray(new LocalDateTime[dates.size()])));
	}

	@DisplayName("UNORDERED TIME Test")
	@Test
	public void testUnorderedTime() {
		List<LocalDateTime> dates = new ArrayList<>();
		//even though unordered, should sort and get 44
		dates.add(LocalDateTime.of(date, LocalTime.of(7, 59)));// 18 select 18 + 18 = 36, check till  < 8:59
		dates.add(LocalDateTime.of(date, LocalTime.of(8, 0)));// 13 skip
		dates.add(LocalDateTime.of(date, LocalTime.of(8, 29)));//13 skip
		dates.add(LocalDateTime.of(date, LocalTime.of(8, 30)));//8 //skip

		dates.add(LocalDateTime.of(date, LocalTime.of(6, 50)));//13 skip, check till 7:50, select highest
		dates.add(LocalDateTime.of(date, LocalTime.of(7, 0)));// 18 select 18

		dates.add(LocalDateTime.of(date, LocalTime.of(8, 58, 59,59)));//8 //skip
		dates.add(LocalDateTime.of(date, LocalTime.of(8, 59)));//8 select 36 + 8 =44
		assertEquals(34, tollFeeCalculator.getTollFee(car, dates.toArray(new LocalDateTime[dates.size()])));

	}

	@DisplayName("MAXIMUM FEE Test")
	@Test
	public void testMaxFee() {
		List<LocalDateTime> dates = new ArrayList<>();
		dates.add(LocalDateTime.of(date, LocalTime.of(7, 0)));//18
		dates.add(LocalDateTime.of(date, LocalTime.of(8, 0)));//13
		dates.add(LocalDateTime.of(date, LocalTime.of(9, 0)));//8
		dates.add(LocalDateTime.of(date, LocalTime.of(14, 0)));//8
		dates.add(LocalDateTime.of(date, LocalTime.of(15, 0)));// 13
		dates.add(LocalDateTime.of(date, LocalTime.of(15, 39)));// 18
		dates.add(LocalDateTime.of(date, LocalTime.of(16, 30)));// 18
		assertEquals(ServiceConstants.MAX_FEE, tollFeeCalculator.getTollFee(car, dates.toArray(new LocalDateTime[dates.size()])));
	}

	@DisplayName("TOLL CALCULATION PEAK TIME Test")
	@Test
	public void testPeakTime() {
		List<LocalDateTime> dates = new ArrayList<>();
		dates.add(LocalDateTime.of(date, LocalTime.of(6, 50)));//8 skip, check till 7:50, select highest
		dates.add(LocalDateTime.of(date, LocalTime.of(7, 0)));// 8 select 8
		dates.add(LocalDateTime.of(date, LocalTime.of(7, 59)));// 18 select 8 + 18 = 26, check till  < 8:59
		dates.add(LocalDateTime.of(date, LocalTime.of(8, 0)));// 13 //skip
		dates.add(LocalDateTime.of(date, LocalTime.of(8, 29)));//13 //skip
		dates.add(LocalDateTime.of(date, LocalTime.of(8, 30)));//8 //skip
		dates.add(LocalDateTime.of(date, LocalTime.of(8, 58, 59,59)));//8 //skip
		dates.add(LocalDateTime.of(date, LocalTime.of(8, 59)));//8 select 26 + 8 =34
		assertEquals(34, tollFeeCalculator.getTollFee(car, dates.toArray(new LocalDateTime[dates.size()])));

	}

	@DisplayName("CALCULATION OF SAME FEE Test")
	@Test
	public void testSameFee() {
		List<LocalDateTime> dates = new ArrayList<>();
		dates.add(LocalDateTime.of(date, LocalTime.of(10, 0)));//8
		dates.add(LocalDateTime.of(date, LocalTime.of(10, 12)));//8
		dates.add(LocalDateTime.of(date, LocalTime.of(13, 0)));//8
		assertEquals(16, tollFeeCalculator.getTollFee(car, dates.toArray(new LocalDateTime[dates.size()])));
	}
}
