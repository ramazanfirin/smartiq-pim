import { ICategory } from 'app/entities/category/category.model';

export interface IProduct {
  id?: number;
  name?: string;
  description?: string | null;
  price?: number;
  stock?: number;
  photoContentType?: string;
  photo?: string;
  category?: ICategory | null;
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public price?: number,
    public stock?: number,
    public photoContentType?: string,
    public photo?: string,
    public category?: ICategory | null
  ) {}
}

export function getProductIdentifier(product: IProduct): number | undefined {
  return product.id;
}
