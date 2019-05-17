-- --------------------------------------------------------
-- Host:                         localhost
-- Versione server:              10.3.13-MariaDB - mariadb.org binary distribution
-- S.O. server:                  Win64
-- HeidiSQL Versione:            10.1.0.5464
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dump dei dati della tabella ricoveri.postimassimireparti: ~3 rows (circa)
/*!40000 ALTER TABLE `postimassimireparti` DISABLE KEYS */;
INSERT IGNORE INTO `postimassimireparti` (`reparto`, `nmax`) VALUES
	('CHIRURGIA', 55),
	('GINECOLOGIA', 100),
	('MEDICINA', 48),
	('NEUROLOGIA', 30),
	('NIDO', 15),
	('ORTOPEDIA', 146),
	('PEDIATRIA', 10),
	('PSICHIATRIA', 25),
	('RIANIMAZIONE', 12),
	('U.T.I.C', 15),
	('UROLOGIA', 45),
	('CARDIOLOGIA', 15);
/*!40000 ALTER TABLE `postimassimireparti` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
