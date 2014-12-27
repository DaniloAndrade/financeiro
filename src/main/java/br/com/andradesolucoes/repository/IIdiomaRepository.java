package br.com.andradesolucoes.repository;

import br.com.andradesolucoes.entitys.Idioma;

public interface IIdiomaRepository {
	Idioma buscarPorISO(String iso);
}
