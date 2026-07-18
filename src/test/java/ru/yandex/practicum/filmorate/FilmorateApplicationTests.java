package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class, MpaDbStorage.class})
class FilmorateApplicationTests {

	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final GenreDbStorage genreStorage;
	private final MpaDbStorage mpaStorage;

	// ==================== USER TESTS ====================

	@Test
	void testCreateAndFindUserById() {
		User user = makeUser("test@mail.ru", "testlogin", "Test User");
		User created = userStorage.create(user);

		Optional<User> found = userStorage.findById(created.getId());
		assertThat(found)
				.isPresent()
				.hasValueSatisfying(u -> {
					assertThat(u.getId()).isEqualTo(created.getId());
					assertThat(u.getEmail()).isEqualTo("test@mail.ru");
					assertThat(u.getLogin()).isEqualTo("testlogin");
				});
	}

	@Test
	void testUpdateUser() {
		User user = userStorage.create(makeUser("update@mail.ru", "updatelogin", "Before"));
		user.setName("After");
		userStorage.update(user);

		Optional<User> found = userStorage.findById(user.getId());
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("After");
	}

	@Test
	void testFindAllUsers() {
		userStorage.create(makeUser("u1@mail.ru", "login1", "User1"));
		userStorage.create(makeUser("u2@mail.ru", "login2", "User2"));

		Collection<User> users = userStorage.findAll();
		assertThat(users).hasSizeGreaterThanOrEqualTo(2);
	}

	@Test
	void testAddAndGetFriends() {
		User user1 = userStorage.create(makeUser("fr1@mail.ru", "friend1", "Friend1"));
		User user2 = userStorage.create(makeUser("fr2@mail.ru", "friend2", "Friend2"));

		userStorage.addFriend(user1.getId(), user2.getId());

		List<User> friends = userStorage.getFriends(user1.getId());
		assertThat(friends).hasSize(1);
		assertThat(friends.get(0).getId()).isEqualTo(user2.getId());
	}

	@Test
	void testRemoveFriend() {
		User user1 = userStorage.create(makeUser("rem1@mail.ru", "remlogin1", "Rem1"));
		User user2 = userStorage.create(makeUser("rem2@mail.ru", "remlogin2", "Rem2"));

		userStorage.addFriend(user1.getId(), user2.getId());
		userStorage.removeFriend(user1.getId(), user2.getId());

		List<User> friends = userStorage.getFriends(user1.getId());
		assertThat(friends).isEmpty();
	}

	@Test
	void testGetCommonFriends() {
		User user1 = userStorage.create(makeUser("cm1@mail.ru", "cmlogin1", "Common1"));
		User user2 = userStorage.create(makeUser("cm2@mail.ru", "cmlogin2", "Common2"));
		User common = userStorage.create(makeUser("cm3@mail.ru", "cmlogin3", "CommonFriend"));

		userStorage.addFriend(user1.getId(), common.getId());
		userStorage.addFriend(user2.getId(), common.getId());

		List<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());
		assertThat(commonFriends).hasSize(1);
		assertThat(commonFriends.get(0).getId()).isEqualTo(common.getId());
	}

	// ==================== FILM TESTS ====================

	@Test
	void testCreateAndFindFilmById() {
		Film film = makeFilm("Test Film", 1);
		Film created = filmStorage.add(film);

		Optional<Film> found = filmStorage.findById(created.getId());
		assertThat(found)
				.isPresent()
				.hasValueSatisfying(f -> {
					assertThat(f.getId()).isEqualTo(created.getId());
					assertThat(f.getName()).isEqualTo("Test Film");
					assertThat(f.getMpa()).isNotNull();
					assertThat(f.getMpa().getId()).isEqualTo(1);
				});
	}

	@Test
	void testUpdateFilm() {
		Film film = filmStorage.add(makeFilm("Before Film", 1));
		film.setName("After Film");
		filmStorage.update(film);

		Optional<Film> found = filmStorage.findById(film.getId());
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("After Film");
	}

	@Test
	void testFindAllFilms() {
		filmStorage.add(makeFilm("Film A", 1));
		filmStorage.add(makeFilm("Film B", 2));

		Collection<Film> films = filmStorage.findAll();
		assertThat(films).hasSizeGreaterThanOrEqualTo(2);
	}

	@Test
	void testAddAndRemoveLike() {
		Film film = filmStorage.add(makeFilm("Like Film", 1));
		User user = userStorage.create(makeUser("like@mail.ru", "likelogin", "LikeUser"));

		filmStorage.addLike(film.getId(), user.getId());
		List<Film> popular = filmStorage.getPopular(10);
		assertThat(popular).isNotEmpty();

		filmStorage.removeLike(film.getId(), user.getId());
	}

	@Test
	void testGetPopularFilms() {
		Film film1 = filmStorage.add(makeFilm("Popular1", 1));
		Film film2 = filmStorage.add(makeFilm("Popular2", 2));
		User user = userStorage.create(makeUser("pop@mail.ru", "poplogin", "PopUser"));

		filmStorage.addLike(film1.getId(), user.getId());

		List<Film> popular = filmStorage.getPopular(10);
		assertThat(popular).isNotEmpty();
		assertThat(popular.get(0).getId()).isEqualTo(film1.getId());
	}

	// ==================== GENRE TESTS ====================

	@Test
	void testFindAllGenres() {
		Collection<?> genres = genreStorage.findAll();
		assertThat(genres).hasSize(6);
	}

	@Test
	void testFindGenreById() {
		var genre = genreStorage.findById(1);
		assertThat(genre).isPresent();
		assertThat(genre.get().getName()).isEqualTo("Комедия");
	}

	@Test
	void testFindGenreByUnknownId() {
		var genre = genreStorage.findById(999);
		assertThat(genre).isEmpty();
	}

	// ==================== MPA TESTS ====================

	@Test
	void testFindAllMpa() {
		Collection<?> mpaList = mpaStorage.findAll();
		assertThat(mpaList).hasSize(5);
	}

	@Test
	void testFindMpaById() {
		var mpa = mpaStorage.findById(1);
		assertThat(mpa).isPresent();
		assertThat(mpa.get().getName()).isEqualTo("G");
	}

	@Test
	void testFindMpaByUnknownId() {
		var mpa = mpaStorage.findById(999);
		assertThat(mpa).isEmpty();
	}

	// ==================== HELPERS ====================

	private User makeUser(String email, String login, String name) {
		return User.builder()
				.email(email)
				.login(login)
				.name(name)
				.birthday(LocalDate.of(1990, 1, 1))
				.build();
	}

	private Film makeFilm(String name, int mpaId) {
		Mpa mpa = new Mpa();
		mpa.setId(mpaId);
		return Film.builder()
				.name(name)
				.description("Test description")
				.releaseDate(LocalDate.of(2000, 1, 1))
				.duration(120)
				.mpa(mpa)
				.build();
	}
}