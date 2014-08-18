package rocklike.plugin.jdt.quickassist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import rocklike.plugin.jdt.quickassist.ColumnDescVO.Column;

public class ColumnDescriptionStore {

	private static final String LOCAL_PATH = "objectHolder/columnDescription";

	public static ColumnDescVO unmarshalToVo(String xml) throws Exception{
	    JAXBContext ctx = JAXBContext.newInstance(ColumnDescVO.class);
	    Unmarshaller um = ctx.createUnmarshaller();
	    ColumnDescVO vo = (ColumnDescVO) um.unmarshal(new StringReader(xml));
	    return vo;
	}

	public static ColumnDescVO unmarshalToVo(File f) throws Exception{
		FileInputStream fis = new FileInputStream(f);
		JAXBContext ctx = JAXBContext.newInstance(ColumnDescVO.class);
		Unmarshaller um = ctx.createUnmarshaller();
		ColumnDescVO vo = (ColumnDescVO) um.unmarshal(fis);
		fis.close();
		return vo;
	}

	public static void saveToLocal(ColumnDescVO vo) throws Exception{
		new File(LOCAL_PATH).getParentFile().mkdirs();
	    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOCAL_PATH));
	    oos.writeObject(vo);
	    oos.close();
	}

	public static ColumnDescVO readFromLocal() throws Exception{
	    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LOCAL_PATH));
	    ColumnDescVO readVo = (ColumnDescVO)ois.readObject();
	    ois.close();
	    return readVo;
	}


	public static String getColumnDescriptionApproximately(String column, ColumnDescVO vo){
		for(Column c : vo.getColumns()){
			if(column.toLowerCase().replaceAll("_","").equals(c.name.toLowerCase().replaceAll("_",""))){
				return c.description;
			}
		}
		return null;
	}


}
