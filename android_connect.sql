-- phpMyAdmin SQL Dump
-- version 3.5.2.2
-- http://www.phpmyadmin.net
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 11-05-2015 a las 05:35:01
-- Versión del servidor: 5.5.27
-- Versión de PHP: 5.4.7

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `android_connect`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `products`
--

CREATE TABLE IF NOT EXISTS `products` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(15) NOT NULL,
  `name` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=71 ;

--
-- Volcado de datos para la tabla `products`
--

INSERT INTO `products` (`id`, `code`, `name`, `price`, `quantity`, `created_at`, `updated_at`) VALUES
(5, 'ESM001', 'Esmeril', 555555.00, 500, '2015-03-24 17:09:43', '0000-00-00 00:00:00'),
(49, 'ALI001', 'Alicate', 999999.00, 500, '2015-04-04 19:21:00', '0000-00-00 00:00:00'),
(51, 'TUB003', 'Tuberia 1/2', 999999.00, 500, '2015-04-04 19:21:00', '0000-00-00 00:00:00'),
(52, 'TOB001', 'Tobo', 999999.00, 500, '2015-04-04 19:21:00', '0000-00-00 00:00:00'),
(53, 'LLA001', 'Llave Inglesa', 999999.00, 500, '2015-04-04 19:21:00', '0000-00-00 00:00:00'),
(54, 'MAN001', 'Manguera', 999999.00, 500, '2015-04-07 14:27:59', '0000-00-00 00:00:00'),
(69, 'TAL001', 'Taladro', 99999.00, 500, '2015-05-09 04:06:30', '0000-00-00 00:00:00'),
(70, 'SIE001', 'Sierra', 99999.00, 500, '2015-05-09 04:16:36', '0000-00-00 00:00:00');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
