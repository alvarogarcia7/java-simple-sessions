import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Either<A,B> {
	interface Function_<T> {
		public void apply(T x);
	}

	private A left = null;
	private B right = null;

	private Either(A a, B b) {
		left = a;
		right = b;
	}

	public static <A> Either left(A a) {
		return new Either<>(a, null);
	}

	public static <B> Either right(B b) {
		return new Either<>(null, b);
	}

	public void fold(Function_<A> ifLeft, Function_<B> ifRight) {
		if (right == null)
			ifLeft.apply(left);
		else
			ifRight.apply(right);
	}

	public static class EitherExample {
		public static void main(String[] args) {
			new EitherExample().act();
		}

		private void act() {
			final ThrowingConsumer<Integer, Integer> evens = this::evenOrFail;
			final List<Either> contests = Arrays.asList(0,1,2,3).stream().map(evens).collect(Collectors.toList());

			System.out.println("In order:");
			print(contests);

			System.out.println();
			System.out.println();
			System.out.println("Grouped by outcome:");
			// This is private access here but could be solved with a getter or using the .fold method
			List<Either> success = contests.stream().filter(x -> x.right != null).collect(Collectors.toList());
			List<Either> error = contests.stream().filter(x -> x.left != null).collect(Collectors.toList());

			print(success);
			print(error);
		}

		private void print(List<Either> values) {
			values.stream().forEach(x -> x.fold(System.out::println, System.out::println));
		}

		private Integer evenOrFail(Integer n) {
			if(n % 2 == 0){
				return n;
			} else {
				throw new RuntimeException("No odd numbers are allowed here: " + n);
			}
		}

		@FunctionalInterface
		public interface ThrowingConsumer<T, R> extends Function<T, Either<Exception, R>> {
			@Override
			default Either<Exception, R> apply(T t) {
				try {
					return Either.right(acceptThrows(t));
				} catch (final Exception e) {
					return Either.left(e);
				}
			}

			R acceptThrows(T elem) throws Exception;
		}
	}
}


