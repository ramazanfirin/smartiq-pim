import { ICategory } from 'app/entities/category/category.model';
import { IBasketItem } from 'app/entities/basket-item/basket-item.model';

export interface IProduct {
  id?: number;
  name?: string;
  description?: string | null;
  price?: number;
  stock?: number;
  photoContentType?: string | null;
  photo?: string | null;
  category?: ICategory | null;
  basketItems?: IBasketItem[] | null;
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public price?: number,
    public stock?: number,
    public photoContentType?: string | null,
    public photo?: string | null,
    public category?: ICategory | null,
    public basketItems?: IBasketItem[] | null
  ) {}
}

export function getProductIdentifier(product: IProduct): number | undefined {
  return product.id;
}
