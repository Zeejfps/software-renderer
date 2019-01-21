package com.zeejfps.sr.utils;

import com.zeejfps.sr.Mesh;
import org.joml.Vector3d;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class OBJImporter {

    public static Mesh load(String path) throws IOException {

        ArrayList<Vector3d> verts = new ArrayList<>();
        ArrayList<Integer> indecies = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while ((line = br.readLine()) != null) {

            String[] tokens = line.split(" ");
            if (tokens[0].equals("v")) {

                float x = Float.parseFloat(tokens[1]);
                float y = Float.parseFloat(tokens[2]);
                float z = Float.parseFloat(tokens[3]);

                verts.add(new Vector3d(x, y, z));

            }
            else if (tokens[0].equals("f")){

                int x = Integer.parseInt(tokens[1]) - 1;
                int y = Integer.parseInt(tokens[2]) - 1;
                int z = Integer.parseInt(tokens[3]) - 1;

                indecies.add(x);
                indecies.add(y);
                indecies.add(z);
            }

        }

        br.close();

        int[] indeci = new int[indecies.size()];
        int index = 0;
        for (Integer i : indecies) {

            indeci[index] = i.intValue();
            index++;

        }

        Mesh m = new Mesh(verts.toArray(new Vector3d[verts.size()]), indeci);
        return m;

    }

}
