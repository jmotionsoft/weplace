package towntalk.mapper;

import towntalk.model.Version;

public interface VersionDao {
	public Version getLastVersion();
}
